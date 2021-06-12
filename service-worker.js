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
    "revision": "25be1db81a3f243adf2b9ffa09e06cc7"
  },
  {
    "url": "adapter/Adapter.html",
    "revision": "07c6425bebf445e3bd28881cd3125dbc"
  },
  {
    "url": "adapter/CustomizedAdapter.html",
    "revision": "a4c99f9d0aa8a7bd432f797babf6ff45"
  },
  {
    "url": "adapter/HttpAdapter.html",
    "revision": "3683e852b060d0da8f386a57b62effdb"
  },
  {
    "url": "adapter/ReverseWebsocketAdapter.html",
    "revision": "6b4c4aa3f16071bf241ac2882343cb8c"
  },
  {
    "url": "adapter/WebhookAdapter.html",
    "revision": "7ecaf9653620050a1475e0416e4eff19"
  },
  {
    "url": "adapter/WebsocketAdapter.html",
    "revision": "8e0ba91f5fa588bf18e6b9cde6b65afd"
  },
  {
    "url": "api/API.html",
    "revision": "3b124d6f65c090b7eaab30617ce31df4"
  },
  {
    "url": "api/EventType.html",
    "revision": "2f1a983a36ae997c40a9a544a8985f73"
  },
  {
    "url": "api/MessageType.html",
    "revision": "f1c0136cdc3c54335a57f146842c50c7"
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
    "url": "assets/js/15.656b30fc.js",
    "revision": "68f055e1ce32da8758d4ea3f8996722d"
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
    "url": "assets/js/app.48012cb1.js",
    "revision": "454783b585cc62ddfd38194a1c6142b3"
  },
  {
    "url": "index.html",
    "revision": "da268b15f5958c6cedb6e79135016831"
  },
  {
    "url": "misc/Migration2.html",
    "revision": "b1b7a919d530c1067a7abd994c39b11c"
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
