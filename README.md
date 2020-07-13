# khttp

khttp is a simple library for HTTP requests in Kotlin. It functions similarly to Python's `requests` module.

```kotlin
import khttp.get

fun main(args: Array<out String>) {
    // Get our IP
    println(get("http://httpbin.org/ip").jsonObject.getString("origin"))
    // Get our IP in a simpler way
    println(get("http://icanhazip.com").text)
}
```

## Use

This for is meant to provide a way to use khttp when using The Central Repository.
Use [the following artifact](https://search.maven.org/artifact/org.danilopianini/khttp)
