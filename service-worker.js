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
    "revision": "d2424191525b60157c1698d6d05b335f"
  },
  {
    "url": "adapter/Adapter.html",
    "revision": "217c45fb01a36ad550df7c9544a9a3d6"
  },
  {
    "url": "adapter/CustomizedAdapter.html",
    "revision": "c9d380572df13ab6917b11773b9568ea"
  },
  {
    "url": "adapter/HttpAdapter.html",
    "revision": "b4fdf3d5f454f42738e3ec961ec16049"
  },
  {
    "url": "adapter/ReverseWebsocketAdapter.html",
    "revision": "f1404039f2451f967b87296793524263"
  },
  {
    "url": "adapter/WebhookAdapter.html",
    "revision": "8d216d2bcbd145a8daf83e3610529a6e"
  },
  {
    "url": "adapter/WebsocketAdapter.html",
    "revision": "77c3fe7b5eea3b44ee29afd72896e784"
  },
  {
    "url": "api/API.html",
    "revision": "e72d38f73d4b35cc9cd73cdbb730c5ff"
  },
  {
    "url": "api/EventType.html",
    "revision": "49e0e53c5e552724e5261bd291d543ca"
  },
  {
    "url": "api/MessageType.html",
    "revision": "f6f47e9409fd586277b67a76398e4ee5"
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
    "url": "assets/js/10.58eeb998.js",
    "revision": "0473cbe28ea1ec5cc78937b8f5524649"
  },
  {
    "url": "assets/js/11.04138e69.js",
    "revision": "328285a69ee753f52da9746a840306f2"
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
    "url": "assets/js/8.fdaa16f4.js",
    "revision": "8533229971c7598aa7cc500c19004297"
  },
  {
    "url": "assets/js/9.d0cb660c.js",
    "revision": "22c140a778dc618d90aad04d6223eb2b"
  },
  {
    "url": "assets/js/app.319f2df4.js",
    "revision": "9e0c195d4cdbffaac297063169e26cd8"
  },
  {
    "url": "index.html",
    "revision": "32f7027dd722f3bbca99551bad9fd86d"
  },
  {
    "url": "misc/Migration2.html",
    "revision": "1c3767578ec85dc8b94c6446d79ebaff"
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
