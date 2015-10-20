.. khttp documentation master file, created by
   sphinx-quickstart on Tue Oct 20 13:22:24 2015.
   You can adapt this file completely to your liking, but it should at least
   contain the root `toctree` directive.

khttp: HTTP without the bullshit
================================

Java is bad at HTTP, and, by extension, Kotlin is bad at HTTP. More and more, the need to interact with APIs and perform
basic HTTP requests shows up in programs, but Java makes a big fuss about it. The tools available in Java were made for
yesterday, and they don't match what programmers need today.

To combat this, libraries like `Unirest <http://unirest.io/java.html>`_ have popped up, but they introduce massive
amounts of overhead to just make a simple GET request.

khttp attempts to solve this problem, by mimicking the Python `requests <http://docs.python-requests.org/en/latest/>`_
module. khttp uses only the ``org.json.json`` library and native Java. There's nothing else. No bullshit.

::

    val r = get("https://api.github.com/user", auth=BasicAuthorization("user", "pass"))
    r.statusCode // 200
    r.headers["Content-Type"] // "application/json; charset=utf8"
    r.text // """{"type": "User"..."""
    r.jsonObject // org.json.JSONObject

Notable variations from requests
================================

khttp attempts to be as close to the requests module as possible, but there are some variations, especially in features
that have yet to be implemented.

* **Requests are not actually made until a property is accessed**

  This is intentional. Due to the way that khttp allows ``initializers`` to set up a connection before it takes place,
  this must remain the way that khttp works. Simply accessing the ``raw`` property of a request will process the
  request, if no further data is needed.

* **There is no encoding property**

  This is something that needs to be tackled. So far, all requests are assumed to be UTF-8.

* **Some parameters are missing**

  The missing parameters from request methods are ``files``, ``proxies``, ``verify``, ``stream``, and ``cert``. These
  are planned for implementation.

* **The json() function is split into two properties: jsonObject and jsonArray**

  There is no unifying JSON interface that both JSONObject and JSONArray implement. Rather than creating a wrapper that
  requires casting and type-checking, there are simply two properties instead of one function.

* **There is no content property**

  Use the ``raw`` property to get an ``InputStream``. Use the ``text`` property to get a ``String``.

* **There is no history property**

  This is planned. It should be very easy, due to the manual handling of redirects necessary to handle cookies, since
  Java is just awful at HTTP.

Notable variations from plain ol' Java
======================================

These are variations in how HTTP requests are handled. Obviously the methods in which the requests are made are very,
very different.

* **HTTP status codes indicative of errors don't throw exceptions**

  ``HttpURLConnection``\ s really don't like it when there are bad status codes. If you try to get the ``InputStream`` of
  any request that has an error status code, an exception will be thrown, making it so the content can't even be seen.

  khttp rectifies this. Go ahead, try ``println(get("https://httpbin.org/status/418").text)`` for a pretty picture of a
  teapot. Of course, if you were using these exceptions to check for errors, now just check the ``statusCode`` property.

* **Cookies persist across redirects**

  Java really, really doesn't handle cookies well. Cookies aren't even handled unless a global manager for all requests
  is set. That's dumb.

  khttp allows for the setting and retrieving of cookies, and they persist through redirects, if redirects are allowed.
  If a ``POST`` request is made to a login form, and a cookie is set before being redirected back to the homepage, that
  cookie will be available in the homepage response.

Contents:

.. toctree::
   :maxdepth: 2



Indices and tables
==================

* :ref:`genindex`
* :ref:`modindex`
* :ref:`search`

