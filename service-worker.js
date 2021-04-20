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
    "revision": "a4971bdb890b343e75bd8c5dd4e1c558"
  },
  {
    "url": "API.html",
    "revision": "0bc34b68626fc5a726ebc45b2b100d66"
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
    "url": "assets/js/10.9d2758e9.js",
    "revision": "75eff01a47196549f3906c3809a1cc54"
  },
  {
    "url": "assets/js/11.36d9e40a.js",
    "revision": "b1246b982625f6cb4bedbed2d568c8a4"
  },
  {
    "url": "assets/js/12.d2794380.js",
    "revision": "8553107168d8cc28351d7933d5e85bee"
  },
  {
    "url": "assets/js/2.eba207a4.js",
    "revision": "6188e751d9ea0a607e2c6d0e1ac2d1b1"
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
    "url": "assets/js/6.2bbbe609.js",
    "revision": "4f68db966b2910d6f2b1bded1c4a787f"
  },
  {
    "url": "assets/js/7.017fe42c.js",
    "revision": "8e0ff14d7a8d14848bd51ff144b7e581"
  },
  {
    "url": "assets/js/8.5e0aa37b.js",
    "revision": "f757878a3408b1ef1b1a8e9ecee92b98"
  },
  {
    "url": "assets/js/9.e4be22f3.js",
    "revision": "cda5b8c991343cae248ea3c12ebba021"
  },
  {
    "url": "assets/js/app.b84b2155.js",
    "revision": "9e6d18dcb1f71fe17fe97863ae29c331"
  },
  {
    "url": "EventType.html",
    "revision": "67dc3b8d4ee3543175b7316cd8a6593a"
  },
  {
    "url": "Heartbeat.html",
    "revision": "336830a45a31bd78eb11c522966e1245"
  },
  {
    "url": "index.html",
    "revision": "568f135ae082c3e041d8d64db060fa3d"
  },
  {
    "url": "MessageType.html",
    "revision": "43b330e4bf9140a82f556905171c6c37"
  },
  {
    "url": "Report.html",
    "revision": "8a7963134675b1055bae9c0753af0163"
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
