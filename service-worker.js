/**
 * Welcome to your Workbox-powered service worker!
 *
 * You'll need to register this file in your web app and you should
 * disable HTTP caching for this file too.
 * See https://goo.gl/nhQhGp
 *
 * The rest of the code is auto-generated. Please don't update this file
 * directly; instead, make changes to your Workbox build configuration
 * and re-run your build process.
 * See https://goo.gl/2aRDsh
 */

importScripts("https://storage.googleapis.com/workbox-cdn/releases/4.3.1/workbox-sw.js");

self.addEventListener('message', (event) => {
  if (event.data && event.data.type === 'SKIP_WAITING') {
    self.skipWaiting();
  }
});

/**
 * The workboxSW.precacheAndRoute() method efficiently caches and responds to
 * requests for URLs in the manifest.
 * See https://goo.gl/S9QRab
 */
self.__precacheManifest = [
  {
    "url": "404.html",
    "revision": "65879a01c460be03cc1a046800c0920a"
  },
  {
    "url": "adapter/Adapter.html",
    "revision": "5c590cc097aba7f56dd7ff0cd32a2d01"
  },
  {
    "url": "adapter/CustomizedAdapter.html",
    "revision": "8215525e4c4db0f20a4d618a6ebe253b"
  },
  {
    "url": "adapter/HttpAdapter.html",
    "revision": "ab788260bc7c69234c96666d2b3d12a6"
  },
  {
    "url": "adapter/ReverseWebsocketAdapter.html",
    "revision": "450a3b7bdcc60649d3e54ddd5900c23e"
  },
  {
    "url": "adapter/WebhookAdapter.html",
    "revision": "e4a5c03dee70f815a6a24bc0205c8a14"
  },
  {
    "url": "adapter/WebsocketAdapter.html",
    "revision": "f724d99b0da7ed69150e04660b12cb02"
  },
  {
    "url": "api/API.html",
    "revision": "dffb9e076e553bf63515a50a680c4914"
  },
  {
    "url": "api/EventType.html",
    "revision": "ed1c50b3152d0b28444c7ba8acb95170"
  },
  {
    "url": "api/MessageType.html",
    "revision": "e21793456d7c00c0b212cb228caa7451"
  },
  {
    "url": "assets/css/0.styles.96f1c4e5.css",
    "revision": "b7f144b0330972e00049a38294438812"
  },
  {
    "url": "assets/img/search.83621669.svg",
    "revision": "83621669651b9a3d4bf64d1a670ad856"
  },
  {
    "url": "assets/img/search.867d45d8.svg",
    "revision": "867d45d8f9c0da0e3e733dd5e7a8d263"
  },
  {
    "url": "assets/js/10.14b72824.js",
    "revision": "1a57e47f333b5b31e74d0da23b3e08ba"
  },
  {
    "url": "assets/js/11.bbd0a4bb.js",
    "revision": "1364d96fe29076bad89d3739fc134d00"
  },
  {
    "url": "assets/js/12.0e498b42.js",
    "revision": "abc9593af89d60ec36024f31528c5952"
  },
  {
    "url": "assets/js/13.f34ef0ae.js",
    "revision": "705ba36737da75a9b517d124ba8697e0"
  },
  {
    "url": "assets/js/14.2f9d8ba0.js",
    "revision": "eff2b345c91cc3a2ea716ab295260208"
  },
  {
    "url": "assets/js/15.d722f267.js",
    "revision": "bb3e48192579b918a732c85550590d4d"
  },
  {
    "url": "assets/js/16.dcee8ece.js",
    "revision": "80252591b8a754a8fcdfba314f8e07a2"
  },
  {
    "url": "assets/js/17.131393ce.js",
    "revision": "ec0cb21d12550fa48283c3b9a1b964d5"
  },
  {
    "url": "assets/js/2.437446ed.js",
    "revision": "523c5543f897f5a689a6d39f0a7a4db9"
  },
  {
    "url": "assets/js/3.2f903010.js",
    "revision": "0bbe095fddc43d227bfa13d535e4a658"
  },
  {
    "url": "assets/js/4.cdfb9bab.js",
    "revision": "9f2963716659a3081c88d11e41db8a56"
  },
  {
    "url": "assets/js/5.f0e7f0af.js",
    "revision": "1da2e4dee4d44c725eea3abcbf71fd1a"
  },
  {
    "url": "assets/js/6.01ad34ea.js",
    "revision": "79e350a53b3feef7192ff02445566a78"
  },
  {
    "url": "assets/js/7.1099cfca.js",
    "revision": "3dccf8c07b174a9df5decc7dc0ba248f"
  },
  {
    "url": "assets/js/8.f9aab0cd.js",
    "revision": "98924ae63af330658f3a3470ebd37438"
  },
  {
    "url": "assets/js/9.8a0a65a7.js",
    "revision": "c7536d089e426559db4c32d74e1398b1"
  },
  {
    "url": "assets/js/app.f2bba1a5.js",
    "revision": "65957bc3e1188be0fe0587a8df923ba2"
  },
  {
    "url": "index.html",
    "revision": "3c07c87ef81df0d0c00c1d4ab7fea4cd"
  },
  {
    "url": "misc/Migration2.html",
    "revision": "75fcfc26b818fa11e36e2433d50ce631"
  }
].concat(self.__precacheManifest || []);
workbox.precaching.precacheAndRoute(self.__precacheManifest, {});
addEventListener('message', event => {
  const replyPort = event.ports[0]
  const message = event.data
  if (replyPort && message && message.type === 'skip-waiting') {
    event.waitUntil(
      self.skipWaiting().then(
        () => replyPort.postMessage({ error: null }),
        error => replyPort.postMessage({ error })
      )
    )
  }
})
