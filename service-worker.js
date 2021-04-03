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
    "revision": "50f7cb6df4c219ceec08fa471513b077"
  },
  {
    "url": "API.html",
    "revision": "f227ad51817d099cb40415bf72ea156f"
  },
  {
    "url": "assets/css/0.styles.0ec3460c.css",
    "revision": "319874252161608990e7c1f12e0f1e5f"
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
    "url": "assets/js/2.09547494.js",
    "revision": "cadc1e1068df4d7072857829a2344a64"
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
    "url": "assets/js/6.24358ea8.js",
    "revision": "9e5ed56c5db9aa6e548ab2631e6c44bf"
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
    "url": "assets/js/9.11b46ea6.js",
    "revision": "e9960547fa84b484dfd80127dbeb7744"
  },
  {
    "url": "assets/js/app.0a87b07e.js",
    "revision": "a264ede545989b8409971ec54db4037d"
  },
  {
    "url": "EventType.html",
    "revision": "6622a4c78d2b0148baff6c38f354121e"
  },
  {
    "url": "Heartbeat.html",
    "revision": "3ce051aba8662e859caa65602d22d90b"
  },
  {
    "url": "index.html",
    "revision": "ceef0cf8b14278bdc822e7fd345a7b15"
  },
  {
    "url": "MessageType.html",
    "revision": "50b39bf2ea8872ddc6d0e16e0a838e5f"
  },
  {
    "url": "Report.html",
    "revision": "c9bc791cee9c53cd1d6ab7ef8693f48d"
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
