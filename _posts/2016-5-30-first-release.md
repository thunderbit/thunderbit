---
title: "First pre-release is out"
categories: [blog]
---
Some days ago we published the first pre-release of Thunderbit, version `0.1`, just in time for the end of [the competition](http://osl.ugr.es/2015/10/01/certamen-de-proyectos-libres-de-la-universidad-de-granada-2015-2016/). It's still in an early stage of development and willing for somebody to test it ;).

We uploaded [a `.zip` file to GitHub](https://github.com/thunderbit/thunderbit/releases/download/v0.1/thunderbit-0.1.zip) with all you need to run it inside, except for the Java Runtime Environment and the PostgreSQL database server. It is way easier to run Thunderbit locally from this package than from the sources.

# What you get with this pre-release

- File storage
    - Local
    - Cloud (Amazon S3)
- Tagging
- Filtering by one or more tags
- Autocomplete tags by name
- Localized UI (Spanish and English)
- User+password protected uploading

## How to set it up

### Prepare the environment

1. Install the Java Runtime Environment (>=1.8)
2. Set up a postgreSQL server (>=9.4) and create a user and a database for Thunderbit

### Run the application

1. [Get the release package from GitHub](https://github.com/thunderbit/thunderbit/releases/download/v0.1/thunderbit-0.1.zip)
2. Extract it somewhere you have write access to
3. Edit the configuration file at `conf/application.conf` and replace database connection settings with yours
4. On this same configuration file change the `play.crypto.secret` key's value to something really secret (eg. "bigmomma" or "1982gonzoopera"), as well as `authentication.username` and `authentication.password`.
5. From a terminal, run `bin/thunderbit` (`bin/thunderbit.bat` if you are on Windows)

Now the application should be available at [http://localhost:9000](http://localhost:9000). Login with the username and password you set before. To stop it enter `Ctl + C` or just close the terminal.

If you stuck somewhere in the installation process open a thread on [the mail list](mailto:thunderbit-dev@googlegroups.com). If you find a bug [fill an issue on Taiga](https://tree.taiga.io/project/thunderbit/issues). If you like what you see [let us know](http://twitter.com/thunderbitdev).

## How to run it on the cloud (Heroku)

Just create an account on Heroku, login, go to [the project's repo on GitHub](https://github.com/thunderbit/thunderbit#how-to-run-it-in-the-cloud) and click the ![Deploy](https://www.herokucdn.com/deploy/button.svg) button. Heroku will do the rest.

## What's next?

Creating the tags recomender have been harder than we thought. It should come out with the next release. Until then you have tags autocompletion by name. We are also working on a `.deb` package, which should make it a breeze to install and manage Thunderbit on Debian and it's derivates.

## Aknowledgements

It has been a six months journey since we started working on Thunderbit to this first release, and we have enjoyed almost everything about it, but there is one thing we have enjoyed the most: community. There is nothing like discovering someone has [starred your project](https://github.com/thunderbit/thunderbit/stargazers), filled an issue, or in any other way shown interest about it. That feeling is just priceless. Thank you a lot for being there with us.

A BIG THANKS to the [Free Software Office of the University of Granada](http://osl.ugr.es) for creating [the competition](http://osl.ugr.es/2015/10/01/certamen-de-proyectos-libres-de-la-universidad-de-granada-2015-2016/), and to the [Taiga](http://taiga.io) team for their support and for being a source of inspiration as a free software project.