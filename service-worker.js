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
    "revision": "c3ebcad8529dbb038da87799efde884b"
  },
  {
    "url": "adapter/Adapter.html",
    "revision": "cb127f85c64a877b859a7a1f89eaa9c1"
  },
  {
    "url": "adapter/CustomizedAdapter.html",
    "revision": "c21ad1e5ea3d41ad6a23a9c308136642"
  },
  {
    "url": "adapter/HttpAdatper.html",
    "revision": "89b740d492b87d6d6ed9a5d984f77c2e"
  },
  {
    "url": "adapter/ReverseWebsocketAdapter.html",
    "revision": "bd9db92d1157b4b56946efa9ff4a6389"
  },
  {
    "url": "adapter/WebhookAdapter.html",
    "revision": "d0de431fa97f74de4b11d32b0f5328b3"
  },
  {
    "url": "adapter/WebsocketAdapter.html",
    "revision": "b731e4f1bad2d599658e2065a0026c73"
  },
  {
    "url": "api/API.html",
    "revision": "7f27bb464fd33632f0dde5ae9a9905e0"
  },
  {
    "url": "api/EventType.html",
    "revision": "25aba712ce198609bd6007d99f21c315"
  },
  {
    "url": "api/MessageType.html",
    "revision": "ceb9dc2073c30475c5c404355fd790dc"
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
    "url": "assets/js/12.9508b5fe.js",
    "revision": "9e25a6d5e97a71d6494b1b98727f36ce"
  },
  {
    "url": "assets/js/13.55b72976.js",
    "revision": "a58e2b696de8ef495e6ee26e902a2995"
  },
  {
    "url": "assets/js/14.57ebb766.js",
    "revision": "d8d82e6f73847f92d5a272b1d0b27230"
  },
  {
    "url": "assets/js/15.27ee1900.js",
    "revision": "f5d8e43f0cbdaf461916143a059a36b2"
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
    "url": "assets/js/6.a3b2666d.js",
    "revision": "8ab82045aeee306d18133c604aad4d1f"
  },
  {
    "url": "assets/js/7.db2d825a.js",
    "revision": "7f43dbe601e69b6c14672bd69bc82270"
  },
  {
    "url": "assets/js/8.f9aab0cd.js",
    "revision": "98924ae63af330658f3a3470ebd37438"
  },
  {
    "url": "assets/js/9.a5fd5a2d.js",
    "revision": "c7536d089e426559db4c32d74e1398b1"
  },
  {
    "url": "assets/js/app.1381ec50.js",
    "revision": "2693efc16c6d72fac505e079ce192a1d"
  },
  {
    "url": "index.html",
    "revision": "fd89220cbb337f537bf41ec98226552f"
  },
  {
    "url": "misc/Migration2.html",
    "revision": "89fa0f83604c99efefc69ab643156ddd"
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
