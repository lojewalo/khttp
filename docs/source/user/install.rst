.. _install:

Installation
============

khttp is a new library, and it isn't on Maven Central, yet. Once it matures a bit, it will be looked into.

JitPack
-------

`JitPack <https://jitpack.io/>`_ is a great service that will build GitHub projects and offer them as a repository for
free. If you want to use khttp in a project, use JitPack.

Stable builds are avalable on JCenter. Some have had more sucess instaling from there.

Maven
-----
JitPack
^^^^^^^^

Add the JitPack repository

.. code-block:: xml
	<repository>
		<id>jitpack.io</id>
		<url>https://jitpack.io</url>
	</repository>

Add the dependency

.. code-block:: xml
	<dependency>
		<groupId>com.github.jkcclemens</groupId>
		<artifactId>khttp</artifactId>
		<version>-SNAPSHOT</version>
	</dependency>

*As khttp has no official releases or tags at the moment, JitPack uses -SNAPSHOT to denote the latest commit.*

JCenter
^^^^^^^^
Add the JCenter repository

.. code-block:: xml
	<repository>
  		<id>jcenter</id>
  		<url>http://jcenter.bintray.com/</url>
	</repository>

Add the dependency

.. code-block:: xml
	<dependency>
		<groupId>khttp</groupId>
		<artifactId>khttp</artifactId>
		<version>1.0.0</version>
	</dependency>



Get the code
------------

khttp is actively developed on GitHub, where the code is `always available <https://github.com/jkcclemens/khttp>`_.

You can either clone the public repository::

    $ git clone git://github.com/jkcclemens/khttp.git

Download the `tarball <https://github.com/jkcclemens/khttp/tarball/master>`_::

    $ curl -OL https://github.com/jkcclemens/khttp/tarball/master

Or, download the `zipball <https://github.com/jkcclemens/khttp/zipball/master>`_::

    $ curl -OL https://github.com/jkcclemens/khttp/zipball/master

