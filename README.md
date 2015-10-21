# khttp
[![Travis CI](https://img.shields.io/travis/jkcclemens/khttp.svg)](https://travis-ci.org/jkcclemens/khttp)
[![Codecov](https://img.shields.io/codecov/c/github/jkcclemens/khttp.svg)](https://codecov.io/github/jkcclemens/khttp)
[![VersionEye](https://www.versioneye.com/user/projects/56243e0a36d0ab0021000bf4/badge.svg?style=flat)](https://www.versioneye.com/user/projects/56243e0a36d0ab0021000bf4)
[![License](https://img.shields.io/github/license/jkcclemens/khttp.svg)](https://github.com/jkcclemens/khttp/blob/master/LICENSE)
[![Gratipay](https://img.shields.io/gratipay/jkcclemens.svg)](https://gratipay.com/~jkcclemens/)
[![Documentation status](https://readthedocs.org/projects/khttp/badge/?version=latest)](http://khttp.readthedocs.org/en/latest/?badge=latest)

khttp is a simple library for HTTP requests in Kotlin. It functions similarly to Python's `requests` module.

```kotlin
import me.kyleclemens.khttp.get

fun main(args: Array<out String>) {
    // Get our IP
    println(get("http://httpbin.org/ip").jsonObject.getString("origin"))
    // Get our IP in a simpler way
    println(get("http://icanhazip.com").text)
}
```

## Dependency

Use [JitPack](https://jitpack.io/#jkcclemens/khttp) to build khttp. The goal is to get khttp into Maven Central at
some point, but it's not quite mature enough yet!
