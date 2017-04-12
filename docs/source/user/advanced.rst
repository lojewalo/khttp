.. _advanced:

Advanced usage
==============

This document covers some of khttp's more advanced features.

Streaming uploads
-----------------

khttp supports streaming uploads, which allow you to send large streams or files without reading them into memory. To
stream and upload, simply provide a ``File`` object for your body:

::

    val file = File("massive-body")
    post("http://some.url/streamed", data = file)

Request and response objects
----------------------------

Whenever a call is made to ``khttp.get()`` and friends you are doing two major things. First, you are constructing a
``Request`` object which will be sent off to a server to request or query some resource. Second, a ``Response`` object
is generated once ``khttp`` gets a response back from the server. The Response object contains all of the information
returned by the server and also contains the ``Request`` object you created originally. Here is a simple request to get
some very important information from Wikipedia's servers::

    val r = khttp.get("http://en.wikipedia.org/wiki/Monty_Python")

If we want to access the headers the server sent back to us, we do this::

    r.headers
    // {X-Cache=cp1053 hit (6), cp1053 frontend hit (229), Server=nginx/1.9.4, X-Content-Type-Options=nosniff, Connection=keep-alive, Last-Modified=Sun, 08 Nov 2015 08:49:30 GMT, Date=Wed, 11 Nov 2015 02:53:20 GMT, Via=1.1 varnish, 1.1 varnish, Accept-Ranges=bytes, X-Varnish=1511544250 1509836954, 3214907854 2875551089, X-UA-Compatible=IE=Edge, Strict-Transport-Security=max-age=31536000; includeSubDomains; preload, Cache-Control=private, s-maxage=0, max-age=0, must-revalidate, Content-language=en, Content-Encoding=gzip, Vary=Accept-Encoding,Cookie, Content-Length=69400, X-Analytics=page_id=18942;ns=0;WMF-Last-Access=11-Nov-2015;https=1, Age=237769, X-Powered-By=HHVM/3.6.5, Content-Type=text/html; charset=UTF-8}

However, if we want to get the headers we sent the server, we simply access the request, and then the request's
headers::

    r.request.headers
    // {Accept=*/*, Accept-Encoding=gzip, deflate, User-Agent=khttp/1.0.0-SNAPSHOT}

Asynchronous Requests
---------------------

An asynchronous request can be performed by prefixing the desired HTTP method with the keyword ``async``. The response information is passed through the ``onResponse`` callback parameter which provides a reference to a ``Response`` object. On the other hand, error information is passed through the ``onError`` callback parameter which provides a reference to a ``Throwable`` object. The default ``onResponse`` callback simply consumes the ``Response`` object while the default ``onError`` callback throws the ``Throwable`` object. Usage without specifying an ``onError`` callback is as follows::

    khttp.async.get("https://www.google.com/", onResponse = {
        println("Status Code: $statusCode")
        println("Response Text: $text")
    })
    // OR ...
    khttp.async.get("https://www.google.com/") {
        println("Status Code: $statusCode")
        println("Response Text: $text")
    }

The ``onError`` callback can be included as follows::

    khttp.async.get("https://www.google.com/", onError = {
        println("Error message: $message")
    }, onResponse = {
        println("Status Code: $statusCode")
        println("Response Text: $text")
    })
    // OR ...
    khttp.async.get("https://www.google.com/", onError = {
        println("Error message: $message")
    }) {
        println("Status Code: $statusCode")
        println("Response Text: $text")
    }
