---
title: How we make thunderbit
---
As promised in the first post, today we are talking about how we make Thunderbit.

We use [Taiga](https://tree.taiga.io/project/greenled-thunderbit-1), an [agile](http://agilemanifesto.com) project management tool which supports [Kanban](http://blog.taiga.io/what-is-kanban.html). With Taiga we manage what we do, when and by whom.

Our source code is free [(as in freedom)](http://www.gnu.org/philosophy/philosophy.html) so we placed it in a public repository. We keep it at [GitHub](http://github.com/thunderbit/thunderbit), and of course, we use [Git](http://git-scm.org), a great CVS trusted by lots of rocking projects like [Linux kernel](https://github.com/torvalds/linux), [jQuery](https://github.com/jquery/jquery), [Bootstrap](), and many other. There you can [get the sources](http://github.com/thunderbit/thunderbit), [see the commits history](https://github.com/thunderbit/thunderbit/commits/master), [fork the project](https://github.com/thunderbit/thunderbit/network), [make pull requests](https://github.com/thunderbit/thunderbit/pulls), [open issues](https://github.com/thunderbit/thunderbit/issues) and also [give us a star](https://github.com/thunderbit/thunderbit/stargazers) if you like what we are doing.

We test our code locally, and in the cloud via [Travis CI](https://travis-ci.org/thunderbit/thunderbit). This continuous integration tool is a neutral place to test our changes and avoid errors like the classic "It works on my computer".

Last, but not least, we have [a running instance](http://thunderbit.herokuapp.com) of the application at [Heroku](https://heroku.com). This instance is connected to an [Amazon S3](http://aws.amazon.com/s3) bucket to store the uploaded files, and to a [MongoDB](http://mongodb.org) database at [MongoLab](http://mongolab.com).

In short:

1. We code and test locally, then we push to GitHub
2. Every time we make a push Travis CI compiles the project and runs tests on it
3. If the code compiles and the tests run successfully the update is deployed to Heroku

This is a very basic [Continuous Deployment](https://en.wikipedia.org/wiki/Continuous_deployment) set up.
