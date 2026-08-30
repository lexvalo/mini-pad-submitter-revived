# Mini PAD Submitter 26.3 Revived — 2026 community fix

> **The story behind this repo:** [Maintaining the code of the man who wrote "How To Write Unmaintainable Code"](https://www.reddit.com/r/programming/comments/1uzadz5/maintaining_the_code_of_the_man_who_wrote_how_to/)

Roedy Green's **Mini PAD Submitter** is a small Java application that has existed since the late 1990s. It was built to submit [PAD files](https://en.wikipedia.org/wiki/Portable_Application_Description) (Portable Application Description — the shareware-era standard for describing Windows and Mac applications to software directories) to multiple download sites at once.

<img width="684" height="517" alt="Mini PAD submitter 26 3 Revived - community edition 2026" src="screenshot.png" />


The final official release (26.3) dates from 2017.

By 2026 the program could no longer communicate with most modern websites. Not because the application itself had become obsolete, but because the web around it had changed. HTTPS became the default, SNI became essential for CDN-hosted websites, and protocol redirects exposed assumptions that had quietly survived for years.

This repository restores compatibility with today's web while preserving the original application and its behavior.

## Timeline

- **Late 1990s** — Mini PAD Submitter first appears.
- **2017** — Last version 26.3 becomes the final official release.
- **2023** — Roedy Green passes away.
- **2026** — This community compatibility update is published.
- **2026** — Roedy's hand-written changelog is [reconstructed into git history](#where-the-git-history-came-from), going back to 1998.

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

3. **The core issue** a static initializer in `Http.java` unconditionally ran `System.setProperty("jsse.enableSNIExtension", "false")` — disabling [SNI](https://en.wikipedia.org/wiki/Server_Name_Indication) for the entire JVM. This was presumably a workaround for some old server in 2014 that choked on it. Today, virtually every site sits behind a CDN (Cloudflare, etc.) that relies on SNI to know _which_ certificate to present on a shared IP. With SNI off, every single HTTPS handshake to a modern site fails with `SSLHandshakeException: Received fatal alert: handshake_failure`. This is what actually blocked things — the first two bugs were just cosmetic on top of this one.

## The fix

- `Submitter.java`: accept `https://` as a valid prefix in both the auto-correct and the validation check
- `Http.java`: stop force-disabling SNI, and manually follow cross-protocol redirects (JDK won't do it for you) by re-opening a connection to the `Location` header when the response is a 301/302/303/307/308

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

1. Install a Java runtime (JRE/JDK 21 or later)
2. Download `mini-pad-submitter.jar` from the latest `Gradle Build` Actions run and [`dist/sites.txt`](dist/sites.txt) into the same folder
3. Run it: `java -jar mini-pad-submitter.jar`. A GUI window opens ("Mini PAD Submitter 26.3 Revived").
4. Fill in two fields:
   - **Web Dir URL** — the folder your PAD file lives in, e.g. `https://yoursite.com` (no trailing filename).
   - **PAD xml file** — just the filename, e.g. `yoursite-pad.xml` (no `http://`, no `/`, no domain — the tool joins the two fields itself).
5. Click **Submit**. It'll try every site listed in `sites.txt` and report which ones accepted it.

`sites.txt` needs to sit next to whichever jar you actually run.

## Building from source

You'll need a JDK version 21 or later.

From the repo root:

```sh
./gradlew build
java -jar app/build/libs/app.jar
```

## Where the git history came from

The commit log goes back to 1998. Roedy used SVN for source control and embedded a changelog at the top of each source file, with comments describing each change along with a version number and date.

[cliserkad](https://github.com/cliserkad) turned those comment lines into commits ([#1](https://github.com/lexvalo/mini-pad-submitter-revived/pull/1)), one per changelog entry, each with its original date and message. The commits are authored as Roedy Green, since the dates and the wording are his; the committer field records cliserkad, who did the reconstruction. The changelog blocks are gone from the file headers now, replaced by a single `Version:` line that moves through the history to match.

Plain `git log` misrepresents the original changelog, since the reconstructed commits were added after the 2026 patches. Sorting by author date puts it in order:

```sh
git log --pretty=format:"%ad %h by %an, %s" --date=iso | sort
```

It also makes for a good [Gource](https://gource.io/) run, with `--author-time`.

If you have a copy of the original source control repository, please open a pull request or issue so it can be integrated. The original SVN host, [wush.net](https://wush.net/wn/home), is no longer operating.

## About the original author

Mini PAD Submitter was written by **Roedy Green** of Canadian Mind Products ([mindprod.com](https://mindprod.com)), a Canadian software developer who spent decades writing and freely distributing Java utilities, and maintaining the widely-read [Java Glossary](https://mindprod.com/jgloss/jgloss.html). Roedy passed away in October 2023, following a stroke.

The original software is still downloadable from his personal site, which remains online. This repository just fixes the one part of it that had stopped working.

Roedy Green also wrote [How To Write Unmaintainable Code](https://github.com/droogans/unmaintainable-code), a famous satirical guide about writing code nobody can maintain. So, today, code he wrote almost three decades ago was maintained.

## License

Roedy Green's original code is distributed under his own terms — see [LICENSE.md](LICENSE.md). The short version: free to use and modify, for any purpose except military use, and that restriction carries forward to anything built on top of it. This repository follows the same terms.
