# [Thunderbit](http://thunderbit.github.io/thunderbit/)

[![Managed with Taiga.io](https://img.shields.io/badge/managed%20with-Taiga.io-green.svg)](https://tree.taiga.io/project/thunderbit/ "Managed with Taiga.io")
[![Build Status](https://travis-ci.org/thunderbit/thunderbit.svg?branch=master)](https://travis-ci.org/thunderbit/thunderbit)
[![Codacy Badge](https://api.codacy.com/project/badge/grade/7ce48d45bf8e4e769217676b3fa259bb)](https://www.codacy.com/app/rickmclean/thunderbit)
[![Heroku Badge](http://heroku-badge.herokuapp.com/?app=thunderbit)](https://thunderbit.herokuapp.com/)

A web application to manage digital assets, with a big emphasis on tagging.

## Motivation

We wanted a system to manage our digital assets where we didn't had to know the name of what we were looking for nor it's location. Such a system should make it easy to discover new content and to find related content. Also, we wanted it to be as simple as possible so anyone could quickly get to use it.

## How it works

You have a bunch of files, say `Mozilla Firefox 44 - EN.zip`, `Google Chrome 17 - ES.exe`, etc. and tags describing them, say `browser`, `firefox`, `chrome`, `mozilla`, `google`, `linux`, `windows`, `64bit` and so on.

When you want to find something you enter the tags that better describes it. As you enter the tags, and based on the files they have in common, Thunderbit recommends you existing tags related to the ones you entered. For example, if you have a GNU/Linux's distributions installation disk images collection (which may become pretty large, by the way :) and you enter `ubuntu` and `64bit`, system could recommend you `gnome`, `desktop` and `server` tags. Later on and having selected `desktop` tag, you can drop `ubuntu` and watch the list of 64-bit disk images for desktop.

Find more at [the project's website](http://thunderbit.github.io/thunderbit/)

## Quick test

We keep a [running instance of Thunderbit](https://thunderbit.herokuapp.com) at Heroku. You can test it with following credentials:

- username: tbit
- password: tbit

To prevent service abuses this instance uses a mock storage module, which will accept uploads but will serve a static mock file. This instance is also cleaned and restarted from time to time.

## How to run it locally

### 1. Set up the development framework

We build Thunderbit with Play! Framework 2.4. There are several ways to get Play!, see [the installation instructions](https://www.playframework.com/documentation/2.4.x/Installing) for details. Also, you are gona need the Java Development Kit 1.8.x.

### 2. Set up the database server

As database server we use PostgreSQL 9.3.

### 3. Adjust application settings

Edit the file `conf/application.conf` and change the database connection settings to match your database server ones.

```
db.default.url="jdbc:postgresql://[server]/[database]"
db.default.username="[user]"
db.default.password="[password]"
```

#### Enable a real storage module (optional)

By default, Thunderbit uses a mock storage module which will accept uploads, but will serve a static mock file. You can change this on `storage.*` configuration keys, for example:

```
storage.type = "local"
# Make sure you have write access to storedFiles
storage.local.path = "path/to/stored/files"
# If the storage directory does not exist, create it
storage.local.createPath = true
```

Or use your favorite bucket at Amazon S3:

```
storage.type = "s3"
storage.s3.accesskey = "myAccessKey"
storage.s3.secretkey = "mySecretKey"
storage.s3.bucket = "bucketForThunderbit"
storage.s3.createBucket = true
```

### 4. Run it

If you installed *Activator* on step 1, execute `activator run` inside the project's root. If you installed *SBT*, execute `sbt run` instead.

## How to run it in the cloud

Thunderbit plays well with [Heroku](https://heroku.com). The easiest way to deploy a new instance is through this [![Deploy](https://www.herokucdn.com/deploy/button.svg)](https://heroku.com/deploy) button. All you need is a free Heroku account.

## Bugs and feature requests

Found a bug or have a feature request? Great! Please first read the [issue guidelines](CONTRIBUTING.md#taiga-issues) and search for [existing and closed issues at Taiga.io](https://tree.taiga.io/project/thunderbit/issues). If your problem or idea is not addressed yet, please open a new issue.

## Contributing

Please read our [contributing guidelines](CONTRIBUTING.md).

After trying NetBeans, Eclipse, ScalaIDE and IntelliJ IDEA, we found the latest has the best support for Play! projects (through the Scala plugin). The open Community Edition of IntelliJ IDEA is by far enough.

## Community

Stay connected:

- Follow [@thunderbitdev on Twitter](https://twitter.com/thunderbitdev)
- Read and subscribe to [the blog](http://thunderbit.github.io/blog)
- Join [the Slack room](https://thunderbit.slack.com)
- Join [the mail list](https://groups.google.com/forum/#!forum/thunderbit-dev)

## Licensing

Code is released under the [GNU Affero General Public License v3](LICENSE).
