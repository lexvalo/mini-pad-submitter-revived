# Mini PAD Submitter 26.3 Revived — 2026 community fix

Roedy Green's **Mini PAD Submitter** is a small Java application that has existed since the late 1990s. It was built to submit [PAD files](https://en.wikipedia.org/wiki/Portable_Application_Description) (Portable Application Description — the shareware-era standard for describing Windows and Mac applications to software directories) to multiple download sites at once.

<img width="684" height="517" alt="Mini PAD submitter 26 3 Revived - community edition 2026" src="https://github.com/user-attachments/assets/b9ceab3c-bfa4-4fa1-aacd-b24a3f8a9559" />


The final official release (26.3) dates from 2017.

By 2026 the program could no longer communicate with most modern websites. Not because the application itself had become obsolete, but because the web around it had changed. HTTPS became the default, SNI became essential for CDN-hosted websites, and protocol redirects exposed assumptions that had quietly survived for years.

This repository restores compatibility with today's web while preserving the original application and its behavior.

## Timeline

- **Late 1990s** — Mini PAD Submitter first appears.
- **2017** — Last version 26.3 becomes the final official release.
- **2023** — Roedy Green passes away.
- **2026** — This community compatibility update is published.

---

## Why this exists

This project started almost by accident. 

I was doing distribution work for [RiverScript](https://riverscript.com) — my app that can record and transcribe system audio on Windows and Mac — and went looking for old-school desktop software directories to list it in. 

That ecosystem (Softpedia, FileHorse, and many others) has existed since the 2000s and used to revolve around PAD files, which were typically submitted in bulk using tools like this one. 

I found a copy of Roedy Green's Mini PAD Submitter sitting around and figured I'd try it instead of submitting to each directory by hand. 

It almost worked. It refused to accept our own site's PAD URL at all — and not just ours: it rejected any modern `https://` link, full stop. 

What initially looked like a simple validation bug turned out to be three separate compatibility issues with the modern web. The goal wasn't to modernize the application or rewrite it—only to restore the functionality it originally had, in a world where HTTPS is now the default.

## What was actually wrong

Three separate bugs, all versions of the same root cause: **this code was written before HTTPS was the default, and nobody ever came back to update it.**

1. **The website-URL field mangled `https://`.** The auto-correct logic only checked for the literal string `http://` — since `https://riverscript.com` doesn't start with that, it got `http://` prepended again, producing `http://https://riverscript.com`.

2. **Even with a bare domain, it only ever built a plain `http://` URL**, and `HttpURLConnection` in Java does not automatically follow a redirect that changes protocol (http → https). Since basically everything is HTTPS-with-a-redirect now (in our case, courtesy of Cloudflare), the tool would fetch the _redirect stub_ instead of the real page and reject it for being too short.

3. **The real one:** a static initializer in `Http.java` unconditionally ran `System.setProperty("jsse.enableSNIExtension", "false")` — disabling [SNI](https://en.wikipedia.org/wiki/Server_Name_Indication) for the entire JVM. This was presumably a workaround for some old server in 2014 that choked on it. Today, virtually every site sits behind a CDN (Cloudflare, etc.) that relies on SNI to know _which_ certificate to present on a shared IP. With SNI off, every single HTTPS handshake to a modern site fails with `SSLHandshakeException: Received fatal alert: handshake_failure`. This is what actually blocked things — the first two bugs were just cosmetic on top of this one.

## The fix

- `Submitter.java`: accept `https://` as a valid prefix in both the auto-correct and the validation check (`patches/Submitter.java.diff`).
- `Http.java`: stop force-disabling SNI, and manually follow cross-protocol redirects (JDK won't do it for you) by re-opening a connection to the `Location` header when the response is a 301/302/303/307/308 (`patches/Http.java.diff`).

With those three changes, the tool can fetch and validate a PAD file hosted on any normal modern HTTPS site again.

`dist/submitter-patched.jar` is the rebuilt, runnable jar (`java -jar submitter-patched.jar`). `src/` has the patched files in full plus `Get.java` unchanged (included because `Submitter` depends on it) — for anyone who wants to rebuild from scratch you'll also need the rest of Roedy Green's `com.mindprod` packages (`common18`, `entities`, `fastcat`, etc.) on the classpath, available from his original distribution.

## What this doesn't fix

Getting a PAD validated at all was the blocker this patch removes. Whether the tool then successfully submits to each site in its target list is a separate, untested question — some of those directories are long dead by now, and it's possible a few of the ones still alive have added bot-protection or CSRF tokens that a 2017-era HTTP client might not get past. This patch only fixes the part that was unconditionally broken for anyone on a modern HTTPS site.

## The site list is now editable

The list of 66 target sites used to be hardcoded as a 1500-line Java `enum` (`SubmissionSite.java`) — every site's name, URL, and form fields baked directly into the source, added and removed by hand since 2009. Changing anything meant editing Java and recompiling.

It's now loaded at runtime from [`dist/sites.txt`](dist/sites.txt), a plain text file that sits next to the jar. Each site is a block like:

```
2Software|http://www.2-software.net/submit.html|/submit.html|POST
    pad_file_url=$PAD
    pad_submitted=Submit PAD File
```

`$PAD` is replaced with the actual PAD file URL at submission time. Add, remove, or edit entries with a plain text editor — no recompilation, just restart the program. All 66 original entries are still there, extracted as-is from the 2017 source, including one (`Download11`) that turns out to have always submitted an empty value instead of the real pad URL — a bug in the original code, kept as-is rather than quietly fixed.

## How to use it

1. Install a Java runtime (JRE/JDK 8 or later) if you don't already have one.
2. Download both [`dist/submitter-patched.jar`](dist/submitter-patched.jar) and [`dist/sites.txt`](dist/sites.txt) into the same folder — the jar looks for `sites.txt` next to itself at startup.
3. Run it: `java -jar submitter-patched.jar`. A GUI window opens ("Mini PAD Submitter 26.3 Revived").
4. Fill in two fields:
   - **Web Dir URL** — the folder your PAD file lives in, e.g. `https://yoursite.com` (no trailing filename).
   - **PAD xml file** — just the filename, e.g. `yoursite-pad.xml` (no `http://`, no `/`, no domain — the tool joins the two fields itself).
5. Click **Submit**. It'll try every site listed in `sites.txt` and report which ones accepted it.

`src/` and `patches/` aren't needed to use the tool — they're there for anyone who wants to see exactly what changed or rebuild it themselves. See [What was actually wrong](#what-was-actually-wrong) above.

## Building from source

You'll need a JDK (8 or later). `Submitter` also depends on the rest of Roedy Green's `com.mindprod` packages (`common18`, `entities`, `fastcat`), which aren't duplicated in this repo — the easiest way to get them is to reuse the already-compiled classes bundled in `dist/submitter-patched.jar`.

From the repo root:

```sh
mkdir build
cd build && jar xf ../dist/submitter-patched.jar && cd ..

javac -cp build -d build src/com/mindprod/http/Http.java src/com/mindprod/http/Get.java src/com/mindprod/submitter/Site.java src/com/mindprod/submitter/Submitter.java

jar cfe submitter-rebuilt.jar com.mindprod.submitter.Submitter -C build .
cp dist/sites.txt .

java -jar submitter-rebuilt.jar
```

This extracts the existing jar's classes into `build/`, recompiles the patched files on top of it (overwriting just those `.class` files), and repackages everything into a fresh runnable jar. `sites.txt` needs to sit next to whichever jar you actually run. Tested end to end on a clean directory before writing this down.

## About the original author

Mini PAD Submitter was written by **Roedy Green** of Canadian Mind Products ([mindprod.com](https://mindprod.com)), a Canadian software developer who spent decades writing and freely distributing Java utilities, and maintaining the widely-read [Java Glossary](https://mindprod.com/jgloss/jgloss.html). Roedy passed away in October 2023, following a stroke.

The original software is still downloadable from his personal site, which remains online. This repository just fixes the one part of it that had stopped working.

Roedy Green also wrote [How To Write Unmaintainable Code](https://github.com/droogans/unmaintainable-code), a famous satirical guide about writing code nobody can maintain. So, today, code he wrote almost three decades ago was maintained.

## License

Roedy Green's original code is distributed under his own terms — see [LICENSE.md](LICENSE.md). The short version: free to use and modify, for any purpose except military use, and that restriction carries forward to anything built on top of it. This repository, and the patch it contains, follows the same terms.
