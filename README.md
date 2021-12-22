## DataStore

DataStore 是 Android Jetpack 的一部分。Jetpack DataStore 是一种数据存储解决方案，允许您使用协议缓冲区存储键值对或类型化对象。DataStore 使用 Kotlin 协程和流程（Flow）以异步、一致的事务方式存储数据。官方建议如果当前在使用 SharedPreferences 存储数据，请考虑迁移到 DataStore。

DataStore 提供两种不同的实现：Preferences DataStore 和 Proto DataStore。

- Preferences DataStore 以键值对的形式存储在本地和 SharedPreferences 类似，此实现不需要预定义的架构，也不确保类型安全。
- Proto DataStore 将数据作为自定义数据类型的实例进行存储。此实现要求您使用协议缓冲区来定义架构，但可以确保类型安全。

### Preferences DataStore 使用方式

先导入依赖

```gradle
dependencies {
  // Preferences DataStore (SharedPreferences like APIs)  
  implementation "androidx.datastore:datastore-preferences:1.0.0-alpha06"
  // Typed DataStore (Typed API surface, such as Proto)
  implementation "androidx.datastore:datastore-core:1.0.0-alpha06"
}  
```

Preferences DataStore 的使用方式如下

```kotlin
//1.构建 DataStore
val dataStore: DataStore<Preferences> = context.createDataStore(name = PREFERENCE_NAME)

//2.Preferences DataStore 以键值对的形式存在本地，需要定义一个 key(比如：KEY_JACKIE)
//Preferences DataStore 中的 key 是 Preferences.Key<T> 类型
val KEY_JACKIE = stringPreferencesKey("username")
GlobalScope.launch {
    //3.存储数据
    dataStore.edit {
        it[KEY_JACKIE] = "jackie"
    }
    //4.获取数据
    val getName = dataStore.data.map {
        it[KEY_JACKIE]
    }.collect{ //flow 调用collect 开始消费数据
        Log.i(TAG, "onCreate: $it")  //打印出 jackie
    }
}
```

需要注意的是读取、写入数据都要在协程中进行，因为 DataStore 是基于 Flow 实现的。也可以看到没有 commit/apply() 方法，同时可以监听到操作成功或者失败结果。

Preferences DataStore 只支持 Int , Long , Boolean , Float , String 键值对数据，适合存储简单、小型的数据，并且不支持局部更新，如果修改了其中一个值，整个文件内容将会被重新序列化。

### SharedPreferences 迁移到 Preferences DataStore

接下来我们来看看 SharedPreferences 迁移到 DataStore，在构建 DataStore 的时候传入 SharedPreferencesMigration，当 DataStore 构建完了之后，需要执行一次读取或者写入操作，即可完成迁移，迁移成功后，会自动删除 SharedPreferences 文件

```kotlin
val dataStoreFromPref = this.createDataStore(name = PREFERENCE_NAME_PREF ,migrations = listOf(SharedPreferencesMigration(this,OLD_PREF_NANE)))
```

我们原本的 SharedPreferences 数据如下

```xml
<?xml version='1.0' encoding='utf-8' standalone='yes' ?>
<map>
    <string name="name">lsm</string>
    <boolean name="male" value="false" />
    <int name="age" value="30" />
    <float name="height" value="175.0" />
</map>
```

原本文件目录如下：

![](https://gitee.com/xingfengwxx/blogImage/raw/master/img/20211222104529.png)

迁移后的文件目录如下：

![](https://gitee.com/xingfengwxx/blogImage/raw/master/img/20211222104602.png)

可以看到迁移后原本的 SharedPreferences 被删除了，同时也可以看到 DataStore 的文件更小一些，在迁移的过程中发现一个有趣的情况，如果直接迁移后并不进行任意值的读取，在对应的目录上找不到迁移后的文件，只有当进行任意值的读取后，才会在对应的目录上找到文件。完整代码如下：

```kotlin
val dataStoreFromPref = this.createDataStore(name = PREFERENCE_NAME_PREF
                    , migrations = listOf(SharedPreferencesMigration(this, OLD_PREF_NANE)))
//迁移后需要手动读取一次，才可以找到迁移的文件            
val KEY_NAME = stringPreferencesKey("name")
GlobalScope.launch { 
    dataStoreFromPref.data.map { 
        it[KEY_NAME]
    }.collect {
        Log.i(TAG, "onCreate: ===============$it")
    }
}
```

下面我们继续来看 Proto DataStore，Proto DataStore 比 Preference DataStore 更加灵活，支持更多的类型

- Preference DataStore 只支持 Int 、 Long 、 Boolean 、 Float 、 String，而 protocol buffers 支持的类型，Proto DataStore 都支持
- Proto DataStore 使用了二进制编码压缩，体积更小，速度比 XML 更快

### Proto DataStore 使用方式

因为 Proto DataStore 是存储类的对象（typed objects ），通过 protocol buffers 将对象序列化存储在本地。

数据序列化常用的方式有 JSON、Protocol Buffers、FlatBuffers。Protocol Buffers 简称 Protobuf，共两个版本 proto2 和 proto3，大多数项目使用的 proto2，两者语法不一致，proto3 简化了 proto2 的语法，提高了开发效率。Proto DataStore 对着两者都支持，我们这里使用 proto 3。

新建Person.proto文件，添加一下内容：

```protobuf
syntax = "proto3";

option java_package = "com.hi.dhl.datastore.protobuf";
option java_outer_classname = "PersonProtos";

message Person {
    // 格式：字段类型 + 字段名称 + 字段编号
    string name = 1;
}
```

syntax ：指定 protobuf 的版本，如果没有指定默认使用 proto2，必须是.proto文件的除空行和注释内容之外的第一行

option ：表示一个可选字段

message 中包含了一个 string 类型的字段(name)。注意 ：= 号后面都跟着一个字段编号

每个字段由三部分组成：字段类型 + 字段名称 + 字段编号，在 Java 中每个字段会被编译成 Java 对象。

这些是简单的语法介绍，然后进行 Build 就可以看到生成的文件。

然后我们再来看具体的使用方式：

```kotlin
//1.构建Proto DataStore
val protoDataStore: DataStore<PersonProtos.Person> = this
    .createDataStore(fileName = "protos_file",serializer = PersonSerializer)

GlobalScope.launch(Dispatchers.IO) {
    protoDataStore.updateData { person ->
        //2.写入数据
        person.toBuilder().setName("jackie").build()
    }

    //3.读取数据
    protoDataStore.data.collect {
        Log.i(TAG, "onCreate: ============"+it.name)
    }

}
```

PersonSerializer 类实现如下：

```kotlin
object PersonSerializer: Serializer<PersonProtos.Person> {
    override val defaultValue: PersonProtos.Person
        get() {
            return PersonProtos.Person.getDefaultInstance()
        }

    override fun readFrom(input: InputStream): PersonProtos.Person {
        try {
            return PersonProtos.Person.parseFrom(input) // 是编译器自动生成的，用于读取并解析 input 的消息
        } catch (exception: Exception) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override fun writeTo(t: PersonProtos.Person, output: OutputStream) =
        t.writeTo(output) // t.writeTo(output) 是编译器自动生成的，用于写入序列化消息
}
```

读取和写入也是都在协程当中，创建的文件在该目录下：

![](https://gitee.com/xingfengwxx/blogImage/raw/master/img/20211222104714.png)

#### SharedPreferences 迁移到 Proto DataStore

接下来我们来看看 SharedPreferences 如何迁移到 Proto DataStore 当中

```kotin
//1.创建映射关系
val sharedPrefsMigration =
    androidx.datastore.migrations.SharedPreferencesMigration<PersonProtos.Person>(this,OLD_PREF_NANE){
        sharedPreferencesView,person ->

        //获取SharedPreferences 数据
        val follow = sharedPreferencesView.getString(NAME,"")
        //写入数据，也就是说将数据映射到对应的类的属性中
        person.toBuilder().setName(follow).build()
    }
//2.构建 Protos DataStore 并传入 sharedPrefsMigration
val protoDataStoreFromPref = this.createDataStore(fileName = "protoDataStoreFile"
    ,serializer = PersonSerializer,migrations = listOf(sharedPrefsMigration))

GlobalScope.launch(Dispatchers.IO) {
    protoDataStoreFromPref.data.map {
        it.name
    }.collect{

    }
}
```

可以看到迁移首先需要创建映射关系，然后构建 Protos DataStore 并传入 sharedPrefsMigration，最后迁移完的 SharedPreferences 会被删除，就算你只迁移了一个数据，整个SharedPreferences 也会被删除，所以迁移是一定要把所有需要的数据都搬过去。最后是迁移后的目录

![](https://gitee.com/xingfengwxx/blogImage/raw/master/img/20211222104818.png)

SharedPreferences vs DataStore 功能对比

![](https://gitee.com/xingfengwxx/blogImage/raw/master/img/20211222104915.png)

#### 应用场景

- Preferences DataStore 以键值对的形式存储在本地和 SharedPreferences 类似，存取一些简单的字段等。
- Proto DataStore 将数据作为自定义数据类型的实例进行存储。可以存取一些复杂的对象，适合保存一些重要对象的保存。