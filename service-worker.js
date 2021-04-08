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
    "revision": "04691351eeb6e1d730a080559d0f5b2c"
  },
  {
    "url": "API.html",
    "revision": "3f2f44c335d29614dc7f3f175a9b6638"
  },
  {
    "url": "assets/css/0.styles.d298e7d4.css",
    "revision": "a9f716797a39868a2c924dbd8700c426"
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
    "url": "assets/js/10.36ea21fb.js",
    "revision": "bdc8a3d476a7864cc06a60f6ac0d3e40"
  },
  {
    "url": "assets/js/11.2903950d.js",
    "revision": "8c3e36601975ebcad9cf6c23dbd346bc"
  },
  {
    "url": "assets/js/12.d2794380.js",
    "revision": "8553107168d8cc28351d7933d5e85bee"
  },
  {
    "url": "assets/js/2.9404be35.js",
    "revision": "3b929f80cc5227a00288a50632d430ce"
  },
  {
    "url": "assets/js/3.1b65837f.js",
    "revision": "fbf58b9ed28208c69a0dc9c6046051fb"
  },
  {
    "url": "assets/js/4.79e06109.js",
    "revision": "d3769e05c7a0efdfebe2179e2bb6e3f0"
  },
  {
    "url": "assets/js/5.ca660288.js",
    "revision": "d12cfe72b062e33b2e1f7293f7169cde"
  },
  {
    "url": "assets/js/6.efe6bdb9.js",
    "revision": "aa3e0c3d3749bb4ee325e3925f7923a6"
  },
  {
    "url": "assets/js/7.017fe42c.js",
    "revision": "8e0ff14d7a8d14848bd51ff144b7e581"
  },
  {
    "url": "assets/js/8.4614edb7.js",
    "revision": "810d737eb5b49e55ef8c20117c81965e"
  },
  {
    "url": "assets/js/9.da6f1be2.js",
    "revision": "6e7832f86894c847e88e5a963e47e140"
  },
  {
    "url": "assets/js/app.7d7fcb20.js",
    "revision": "4a7a30c10791175a1494ce7bac772303"
  },
  {
    "url": "EventType.html",
    "revision": "ab93facd7f8017cc7e0e2548af11b3ff"
  },
  {
    "url": "Heartbeat.html",
    "revision": "3e1c907a1904f986f79f733eca311bd3"
  },
  {
    "url": "index.html",
    "revision": "3e0c2f52461a653a2be62c3de9eba620"
  },
  {
    "url": "MessageType.html",
    "revision": "64cf0a06a38b2035ccbcb2ac138662ef"
  },
  {
    "url": "Report.html",
    "revision": "09b5cf103aea6580f9de98f142854972"
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
