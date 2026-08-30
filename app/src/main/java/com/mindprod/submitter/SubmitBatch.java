/*
 * [SubmitBatch.java]
 *
 * Summary: Submits list of pads to various websites. list of URLS starting with http: lead ; means treat as comment.
 *
 * Copyright: (c) 2007-2017 Roedy Green, Canadian Mind Products, http://mindprod.com
 *
 * Licence: This software may be copied and used freely for any purpose but military.
 *          http://mindprod.com/contact/nonmil.html
 *
 * Requires: JDK 1.8+
 *
 * Created with: JetBrains IntelliJ IDEA IDE http://www.jetbrains.com/idea/
 *
 * Version History:
 *  4.3 2009-04-11 add AivSoft, FilesGuard, FreeFileSeek, GetAllSoft, HotFileDownload,
 *                 eek4Software, SharewarePower, Soft-db, Softake, Softholm, SoftLookup
 *  4.4 2009-05-11 remove DL Daddy, add Softwarelode, Digimodes, Download-by
 *  4.5 2009-05-19 remove TrialFiles, add Publish-Me, AlphaDownloads , DownloadChoice, SoftwareArchiveIsGreat,
 *                 Download5000, DownloadArsivi, DownloadShareware, DownloadStation,
 *                 EliasSoftDownloads, FreeSoftwareSharewareDownloads,
 *                 FreeShareWeb, FreewareArchiv, Freeware1,
 *                 FreeSoftwareApps, Goooggle, SafeFreeDownloads, SafeFreeSoftware, SafeFreeSoftwareDowload
 *  4.6 2009-05-19 remove WebAcclaim, add PadfileInfo, PadFM, PeachSeed, ShareApple, Vandino, Webzf
 *  4.7 2009-06-06 add Geeez, GeneralShareware, Newsoft2006, pc24hours,
 *                 ResourceDB, RetailerDeals, Share32, SharewareBay, SharewareKing, SoftAllWare, BobSoft, SoftLow
 *  4.8 2009-07-11 remove BobSoft, EnterHelp, Softake
 *                 add Top4Download, SoftwareMass, SoftwareSizzle, SuperDownloads, TrialWare, TryingBuying, WestDownload
 *  4.9 2009-07-30 remove BestSoftware, add SubmitPadFile, ProgrammersHeaven
 *  5.0 2009-09-22 remove DownloadWast, Seek4Software. CSV files to track hassle and hassle-free.
 *                 Prober discards sites already processed.
 *                 add GeneralFreeware, Enterhelp, Bobsoft, SoftCrown, Softmerge
 *  5.1 2009-10-25 remove Techwoods, add SuperWebHunt, ABDownloads, Downloads2K, Soft321.
 *  5.2 2009-11-03 remove Sharewareville. Add Windows7Download, SoftwareDownloads
 *  5.3 2009-11-21 remove http://www.allapp.com/Submit-Software/
 *                 add http://www.5moons.net/submit.php
 *                 http://www.8844download.com/submit.htm
 *                 http://www.affiliate-referrals.net/submit.php (DLDaddy)
 *                 http://www.goodownload.com/submit.html
 *                 http://www.resourcefill.com/submit.php
 *                 http://www.uniqueidea.net/download/submit.asp
 *                 http://www.acidfiles.com/submit.html
 *  5.4 2009-12-02 remove FileVolution, add Download11, SoftCab, AfDown,
 *  5.5 2009-12-11 add 11 sites: DownloadDir, EuSoftNet, FreeSoftwarePrograms, IMfreeware, dlTube,
 *                 MySoftwareList, NewDownload, SoftwareCrown, PeterBurgess, SearchSomeSoft, SearchAllSoft
 *  5.6 2009-12-13 add 10 sites: 4software2Download, SharewareCheap, SoftwareRatings,
 *                 ShopLagom, SmallFreeware, Soft4Sale, SoftMobile, SoftwareMatrix, SystemUtils, WorldSoftwareArchive
 *  5.7 2009-12-17 add 16 sites: 123Freesoft, 4software2Download, DownloadYourSoftware, EzySoft, FastShareware,
 *                 FileEdge, FilePicks, FileProfile, LoadFree, , ObtainSoft, ReviewWorld, Download4a,
 *                 DownloadExpo, DownloadHeaven, DownloadPile, EasyFileDownloads
 *  5.8 2009-12-18 add 10 sites: FilePile, FilesStore, FindBestSoft, FineDownloads,
 *                 FreewareDump, FreewareSoft, MetaDownloads, PocketPCSoftwareDownloads, Run2, SafeFreeSoftware
 *  5.9 2009-12-19 add 14 sites: SharewareDump, Sharewareville, Smilestone, SoftDir, SoftwareDetails,
 *                 SoftwareKB, SoftWeb, TechWoods, Telecharger, TopSharewareDownloads, VideoSoftwareDirect,
 *                 WinColors, WindFile, BigSoftwareBox
 *  6.0 2009-12-29 remove 1 site, DownloadExpo
 *  6.1 2009-12-31 remove 2 site, Bull, SoftwareDownloads
 *  6.2 2010-01-02 add 1 site, TrinityFiles.
 *  6.3 2010-01-19 drop two sites TrinityFiles, SharewareRatings.com
 *  6.4 2010-02-12 drop newsoft2006.
 *  6.5 2010-02-14 add AbsoluteWay, DownloadNew, DownloadSharewares, EspanolSoftware, Mvbbb aka AllFreeSoftware
 *  6.6 2010-02-15 add FreeSafeSoft, UKSoftwareDownloads, drop WillemsSoft
 *  6.7 2010-02-24 drop BigSoftwareBox
 *  6.8 2010-03-23 drop ShellTips, Gooogle
 *  6.9 2010-03-28 drop EnterHelp SafeFreeSoftware, PublishMe, FreeSoftwareApps
 *  7.0 2010-04-02 drop Softweb
 *  7.1 2010-04-28 drop EuSoftNet, add YankeeDownload.
 *  7.2 2010-05-18 drop 12buzz
 *  7.3 2010-05-26 drop ActiveMerge, SoftMerge
 *  7.4 2010-06-03 drop UKSoftwareDownloads
 *  7.5 2010-06-05 drop free-pad-database.com
 *  7.6 2010-06-07 drop submit-pad-file
 *  7.7 2010-06-30 drop software-online.smilestone.it
 *  7.8 2010-08-03 drop load-free.net
 *  7.9 2010-08-05 allow you to specify directory where logs go on submitter command line.
 *  8.0 2010-08-09 add Ware23, ZoomLoad
 *  8.1 2010-08-15 remove BetterWindowsSoftware
 *  8.2 2010-08-15 fix bug in command line option to let you select logging directory.
 *  8.3 2010-09-12 remove superwebhunt and file-store.info.
 *  8.4 2010-09-14 remove 8844download.com.
 *  8.5 2010-09-19 remove hotfiledownload
 *  8.6 2010-09-24 remove freeshareweb
 *  8.7 2010-10-03 remove software-archive.isgreat.org diamondslastforever.com
 *  8.8 2010-10-19 remove DesktopShareware
 *  8.9 2010-11-01 remove SharewarePower
 *  9.0 2010-11-05 remove AtomicDownload
 *  9.1 2010-11-14 remove download-heaven.info finedownloads.info
 *  9.2 2010-11-23 remove SoftwareMass DownloadShareware DeltaLoad
 *  9.3 2010-12-07 remove 3 dead sites, update code to submit to several sites
 *  9.4 2010-12-17 remove GooDownload
 *  9.5 2010-12-30 remove EliasSoftDownloads
 *  9.6 2011-02-06 remove SharewareDump, Dalexis, 5Moons
 *  9.7 2011-02-23 remove PadfileInfo, add FindSoft
 *  9.8 2011-03-04 add DownloadZNow
 *  9.9 2011-03-09 add DownloadTyphoon
 * 10.0 2011-03-13 add SoftwareWagon
 * 10.1 2011-03-13 add DownloadKaleidoscope
 * 10.2 2011-03-15 add ABCDatos, BgSoft, CyhNet, Discoveres, DownloadUp,
 *                 SoftListDe, SoftListRu, SoftListWs, TrinityFiles remove AbsoluteWay
 * 10.3 2011-04-04 remove FreeSafeSoft, FilePile, SoftwareDetails. All went out of business.
 * 10.4 2011-04-05 add PaulsPicks, update code for ResourceDB
 * 10.5 2011-04-17 remove Softcrown. add FreewareSoftwareLinks
 * 10.6 2011-05-05 remove Techwoods. Add ActiveMerge, AbArchive
 * 10.7 2011-05-20 remove 4Software2Download, Softdir, Softholm, AbArchive
 *                 add DownloadPocket, NewSoft4You, 4Software2Download (add back), AboutVideoConverter,
 *                 Andra, ANewDownload, ApbspotNnet84, AppleTVConverter, AskedFiles, Augesoft
 *                 B3Hostings, BuyAllSoft
 * 10.8 2011-05-22 add CyberMethexis, CriticalFiles, DesktopSoftware, DivX, DivXConverter, Download4Sure,
 *                 DownloadsArea, EFreshWare, 4Software2Download, ANewDownload
 * 10.9 2011-05-23 add FileSearch, FilesPlaza, FindAllSoft, FindSoftwareeu, FineDownloads,
 *                 ForMac, FreeDownloadBusiness, FreeDownloadDevelopment, FreeDownloadEducation, FreeDownloadGraphics,
 *                 FreeDownloadHomeHobby, FreeDownloadUtilities, FreeSafeSoft, FreeSoft,
 *                 FreeSoftwareDownloads, FreewaresZDown, GetABest, Idv, iPadSoftOnline,
 *                 iPhoneToPC, iPodTouchToComputer, MlbSoft, MpegConverter, MsKpl
 *                 OnlyFreeSoft, PandaFiles, Place77, PocketPCFreeware, PopScript
 * 11.0 2011-05-26 add RecoveryReview, RMConverter, ScienceVitolab, SearchAnySoft, SharewareFiles,
 *                 Soft4Buy, SoftDirectoryInfo, SoftFilesUs, SoftFolder, SoftGeeks
 *                 Softholm, SoftMerge, SoftwareDownloadsFree, SoftWebsNepal, Softzu,
 *                 StyleXP3x, SuccessScripts, SwissListe, TipCase, UuuCom,
 *                 WebByTools, Wersoft, Win7Freeware, Windows7software, WinLoad,
 *                 WordPerfect
 * 11.1 2011-05-28 correct form action code used to submit to:
 *                 4Software2Download, AppleTVConverter, Augesoft, B3Hostings,
 *                 DesktopSoftware, DivXConverter, FileSearch, WinLoad
 *                 remove Download4Sure (keeps giving 500 internal server error)
 *                 move EFreshware to hassle list for using hidden validation
 *                 remove FilesPlaza, not responding
 *                 remove Andra, not responding.
 * 11.2 2011-05-31 drop SystemUtils
 * 11.3 2011-06-11 drop FreewareSoft, SoftwareDownloadsFree
 * 11.4 2011-07-12 drop SoftDirectoryInfo
 * 11.5 2011-07-14 drop iPadSoftOnline, iPhoneToPC, iPodTouchToComputer
 * 11.6 2011-07-23 drop AmazingDownloads, add AEoid
 * 11.7 2011-08-14 drop SuccessScripts, Windows7Software
 * 11.8 2011-08-20 drop ANewDownload
 * 11.9 2011-08-24 drop MsKpl
 * 12.0 2011-08-30 update access technique for ActiveMerge, AbsolutelyFreeSoftware, AsfConverter
 *                 drop DownloadChoice, WestDownload, Zoomload
 * 12.1 2011-08-31 update access technique for SoftLookup, TopSharewareDownloads
 *                 drop TipCase, FreewareArchiv, DownloadFrenzy, ByteFlow.
 *                 No longer attempt to render HTML responses. They were jamming Java HTML rendering.
 * 12.2 2011-09-17 remove DownloadPile,FineDownloads,MetaDownloads
 * 12.3 2011-09-18 remove FreewareTown, add SharewareIsland
 * 12.4 2011-09-21 remove AugeSoft
 * 12.5 2011-09-23 remove DownloadStation
 * 12.6 2011-09-25 remove appletvconverter, AsfConverter ,DivXConverter, MpegConverter, RMConverter
 * 12.7 2011-09-28 remove absolutelyfreesoftware
 * 12.8 2011-10-02 remove DownloadPocket
 * 12.9 2011-10-04 remove Digimodes
 * 13.0 2011-10-11 add FiveMoons, Acritum, Avi0, Dalexis, DeltaLoad, DownloadPronet, DownloadSoftFiles, Descargar24
 * 13.1 2011-10-14 add FilesSpot, FreeDownloadSoft, FreewareShareNet, iPadSoftOnline, iPhoneToPC,
 *                 IPodTouchtoComputer, MyFilesNet, MyFreewares, MyGameDownload, Phelios
 *                 SafeFreeSoftware, Zonator, YourFreeFiles, SuperWebHunt
 * 13.2 2011-10-15 drop AlphaDownload FreeFileSeek
 * 13.3 2011-10-24 drop ApbspotNet84
 * 13.4 2011-10-27 drop 4Software2Download
 * 13.5 2011-11-08 drop WebZf
 * 13.6 2011-11-14 drop PadRing padsite, not PADRING tho multi-pad submit scheme, drop SafeFreeSoftware
 * 13.7 2011-11-21 drop DownloadYourSoftware
 * 13.8 2011-11-22 drop BobSoft
 * 13.9 2011-12-04 drop PopScript
 * 14.0 2011-12-11 drop DeltaLoad
 * 14.1 2011-12-23 drop Descargar24
 * 14.2 2012-01-11 drop desktopsoftware.info
 * 14.3 2012-01-17 add DownloadFreePrograms
 * 14.4 2012-01-17 drop Dalexis
 * 14.5 2012-02-06 add EasyFindSoft, Evocero, LQKO, MyDownloadPlanet,
 *                 PlanetSofts, SearchForSoft, Soft112, SoftCrave, SoftMont,
 *                 Software4sure, TrueSoft, FileStorageDe, NebulaShareware, Download3kRo,
 *                 Win7programs
 * 14.6 2012-02-17 drop GetABest
 * 14.7 2012-02-19 resurrect Dalexis, FreeFileSeek, Webzf
 * 14.8 2012-02-21 drop TrueSoft
 * 14.9 2012-02-22 drop EspanolSoftware, GetSharewareForFree
 * 15.0 2012-02-29 drop FreeFileSeek
 * 15.1 2012-03-08 drop 15 sites: Download5000, WebbyTools, FreeSoft, iPadSoftOnline, iPodTouchtoComputer,
 *                 MyGameDownload, NewDownload, ObtainSoft, ResourceDB, ShareSoftware24, SharewareKing, Software4sure,
 *                 SoftwareHorizon, DLDaddy, Acritum
 * 15.2 2012-03-14 drop SmallFreeware
 * 15.3 2012-03-20 drop free-software-downloads and free-software-shareware-downloads
 * 15.4 2012-04-16 drop http://www.softpc.net http://www.soft112.com
 * 15.5 2012-04-19 drop http://www.soft4sale.com/
 * 15.6 2012-04-24 drop AboutVideoConverter, freewaresoftwarelinks
 * 15.7 2012-05-13 drop Zonator, retailerdeals
 * 15.8 2012-05-17 drop DownBroad
 * 15.9 2012-05-27 add All4Down DownloadNew EzSoft Jqwn SoftwareDownloads Swdb TeraByte TheNetFile TheSharewareSpot
 * 16.0 2012-05-31 drop EasyFindSoft
 * 16.1 2012-06-09 drop TryingBuying
 * 16.2 2012-06-11 drop DownloadNew, add Filedir
 * 16.3 2012-06-16 drop downloaddir.com, cybermethexis.org
 * 16.4 2012-06-19 drop software.idv.hk
 * 16.5 2012-06-24 drop FileSearch
 * 16.6 2012-06-26 drop b3hostings, add CuteShareware SearchSoftware
 * 16.7 2012-07-10 drop share32
 * 16.8 2012-07-11 drop searchforsoft
 * 16.9 2012-07-12 drop PandaFiles
 * 17.0 2012-07-21 drop PC24Hours
 * 17.1 2012-07-22 drop all4down
 * 17.2 2012-07-23 drop run2, add Adpocket
 * 17.3 2012-08-08 remove activemerge, softmerge, ozysoftware
 * 17.4 2012-08-29 remove superwebhunt
 * 17.5 2012-09-08 remove cyhnet, add AppsPalette, DownloadMaxi, SoftList, EnterHelp, Site90, SoftPyro
 * 17.6 2012-09-10 remove AdPocket
 * 17.7 2012-09-17 remove EnterHelp
 * 17.8 2012-09-27 remove downloads.adv.site90.net
 * 17.9 2012-10-13 update URL for FileDir
 * 18.0 2012-10-17 drop Dalexis
 * 18.1 2012-10-24 fix Nebulashare, drop superdownloads, aivsoft, telecharger
 *                 add All4Down, CyberMethexis, FreeSoftware911, SuperWebHunt
 *                 add ResourceDB, Site90, TrueSoft, OzySoftware
 * 18.2 2012-10-31 ComAtoZ, FreeSoftwareDownloads, FreeSoftwareSharewareDownloads.
 *                 Update TrueSoft, remove Peachseed.
 * 18.3 2012-11-07 delete Phelios, rename DownloadReady to Sopcos
 * 18.4 2012-11-09 rewrite internals to consistently use enums instead of Strings
 * 18.5 2012-11-23 delete Nebula, CyberMethexis, DownloadMaxi, update code for Truesoft
 * 18.6 2012-12-02 drop Sopcos
 * 18.7 2012-12-05 drop Wincolors, add HotDigitalProducts, add SoftwarePreviews
 * 18.8 2012-12-13 drop Superwebhunt
 * 18.9 2012-12-14 drop searchsomesoft, add Sopcos
 * 19.0 2012-12-18 drop shoplagom
 * 19.1 2013-01-05 drop 123freesoft, atoz.com,
 *                 add getsharewareforfree.com, soft112.com, sopcos.com, searchsomesoft.com
 * 19.2 2013-01-30 drop aeoid, downloadpronet, downloadsoftfiles, filesspot, freewaresharenet
 *                 myfilesnet, softcab, thenetfile, thesharewarespot, sciencevitolab
 * 19.3 2013-01-30 add SoftwareListing, FilesShareware
 * 19.4 2013-02-03 drop acidfiles, fileedge, windfile
 * 19.5 2013-02-08 drop criticalfiles
 * 19.6 2013-02-18 add DownloadReady, Telecharger
 * 19.7 2013-02-22 drop filestorage.de
 * 19.8 2013-03-08 drop softwaredownloads.me, add MacSoftware911, HotCart, Linux112, Windows8Downloads
 * 19.9 2013-03-11 drop All4Down
 * 20.0 2013-03-25 drop DNKA
 * 20.1 2013-03-26 drop site90.net, add 50ftwares.com
 * 20.2 2013-04-09 drop download90.netne.net
 * 20.3 2013-04-20 drop ezy-soft, truesoft
 * 20.4 2013-04-21 drop Sopcos
 * 20.5 2013-04-27 drop freesoftware911 , linux112 , macsoftware911, soft112
 * 20.6 2013-05-05 drop SoftZu, DownloadSoftwareSearch, SoftwareMatrix, SoftwareLocator, EzSoft
 * 20.7 2013-05-05 drop free-software-programs, add AbyanSoft, Software2D, WorldShareware, Infojateng
 * 20.8 2013-05-16 drop place77
 * 20.9 2013-06-03 drop i-freeware-download, software2d, worldshareware.info
 * 21.0 2013-06-15 drop onlyfreesoft, softcrave, infojateng
 * 21.1 2013-07-07 drop abyansoft
 * 21.2 2013-07-24 drop softwebsnepal, trialware.biz add 2Software.
 * 21.3 2013-08-08 drop uniqueideo.net
 * 21.4 2013-08-23 drop bg-soft.net/, newsoft4you , earchsoftwar.com, www.winload.
 * 21.5 2013-09-10 drop www.yourfreefiles.com.
 * 21.6 2013-10-20 drop softwaresizzle, filepicks, hot-cart,
 *                 free-download-soft, programmersheaven, topsharewaredownloads
 * 21.7 2013-11-29 drop imoosoft, softlookup
 * 21.8 2013-12-22 drop soft-all-ware and vadino
 * 21.9 2014-01-07 drop ozysoftware, download-up, review-world, software-listing
 * 22.0 2014-01-21 drop mysharewares,50ftwares, add Apps112,Download100,Download3Kde,Download3Kes
 *                 GetUNet,ScottBurchfield,Soft2Download,SystemPrograms,TheDownloadFree,WindowsSoftware911
 * 22.1 2014-03-06 drop TrinityFiles, GetUnet, PadRepository, Geeez, add Softcrave
 * 22.2 2014-03-24 drop FindBestSoft, softwarepreviews, cuteshareware
 * 22.3 2014-03-31 add 5oftwares, drop ScottBurchfield
 * 22.4 2014-04-07 drop SoftGeeks, HotDigital Products.
 * 22.5 2014-04-23 drop apps112 and SystemPrograms.
 * 22.6 2014-05-15 drop free-software-downloads and softlow.
 * 22.7 2014-06-13 add CoolTechsOftWare, add AppVisor category.
 * 22.8 2014-06-24 drop 5ofttares.com.
 * 22.9 2014-07-01 drop http://www.pocket-pc-freeware.com/
 * 23.0 2014-08-16 add TestBD, BgSoft, AllSoftWares, FileLook,
 *                 SpacyHost, WebContentSolutions, SoftMerge,
 *                 Business112, Linux112, MacSoftware911, Soft112, TrinityFiles,
 *                 Apps112
 *                 drop DownloadFreePrograms
 * 23.1 2014-08-27 drop sharewarebay, pocketpc-software-downlsoads
 * 23.2 2014-09-06 drop dltube, add MatchGameR, DownloadFreeProgram
 * 23.3 2014-09-23 drop 3area.com lqko.com Apps112, Business112, Linux112, MacSoftware911, WindowsSoftware911
 * 23.4 2014-10-15 drop buyAllsoft findallsoft getsharewareforfree padfm Searchanysoft
 *                 Searchsomesoft Searchallsoft Soft-mobile soft2down softwareul spacyhost
 *                 wersoft world-software
 * 23.5 2014-11-28 drop sharewarefiles.net, cooltechsoftware.com, jqwn, qdyu
 *                 add TryBeforePay, Download3KFr, IfBit
 * 23.6 2015-01-06 adjust SoftwareKB. Drop softfiles.us wordperfect.org matchgamer.info
 * 23.7 2015-01-26 drop absoluteshareware, amazingfiles, avi0, ifbit, PeterBurgess, add Liotron
 * 23.8 2015-02-07 drop bestsecuritytips, downloadery, downloadry, generalfreeware, softwarecrown, win7freeware, win7programs
 * 23.9 2015-02-21 drop add spotpig, allfreedownloads, drop divx.ws
 * 24.0 2015-03-26 drop mysoftwarelist, filedomain, bg-soft, trybeforepay. add SaveFile, StandaloneInstaller
 * 24.1 2015-05-17 drop myfreewares, Softmont, Mayzer
 * 24.2 2015-07-21 drop swissliste fd4a downloadznow uuucom freedownloadbusiness freedownloaddevelopment
 *                 freedownloadeducation freedownloadgraphics freedownloadhomehobby freedownloadutilities
 * 24.3 2015-08-21 drop filelook, liotron, fd4a, getallsoft, WebZf, downloadarsivi
 * 24.4 2015-10-18 drop softcrave appspalette savefile add bigdreamsoft freedownloadssclub
 * 24.5 2015-11-29 drop mvbbb, sharewarecheap, ware23, sharewareisland
 * 24.6 2015-12-24 drop bestsoftwarefordownload, bestvistadownloads, freedownloadsclub, top4download, windows7download, windows8downoads
 * 24.7 2015-12-28 drop bestsoftware4download
 * 24.8 2016-02-27 drop allfreedownloads.org, add windows10
 * 24.9 2016-04-15 drop Vonna
 * 25.0 2016-04-30 drop shareapple and trinity and add Isharesoftware.
 * 25.1 2016-05-07 drop easyfiledownloads, softmerge, and Isharesoftware.
 * 25.2 2016-05-20 drop softliste.de, videsoftwaredirect
 * 25.3 2016-06-09 drop Soft-DB
 * 25.4 2016-06-23 drop hame-software and 5moons
 * 25.5 2016-06-25 drop resourcefill
 * 25.6 2016-07-15 drop pluspro.net
 * 25.7 2016-08-06 drop softpyro.com
 * 25.8 2016-08-22 add RaritySoft
 * 25.9 2016-11-10 drop softwarelode, downloadfreeprgrograms, download-it-now.net
 * 26.0 2016-12-05 add SoftwareBee, drop discoveres.com
 * 26.1 2017-01-10 drap thedownloadfree
 * 26.2 2017-02-14 drop mlbsoft and afdown
 * 26.3 2017-03-30 drop http://paulspicks.com/
 */
package com.mindprod.submitter;

import com.mindprod.http.Post;

import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static java.lang.System.*;

/**
 * Submits list of pads to various websites. list of URLS starting with http: lead ; means treat as comment.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 26.3 2017-03-30 drop http://paulspicks.com/
 * @since 2007
 */
@SuppressWarnings( { "FieldCanBeLocal", "WeakerAccess" } )
public final class SubmitBatch
    {
    private static final int FIRST_COPYRIGHT_YEAR = 2007;

    /**
     * not displayed copyright
     */
    @SuppressWarnings( { "UnusedDeclaration" } )
    private static final String EMBEDDED_COPYRIGHT =
            "Copyright: (c) 2007-2017 Roedy Green, Canadian Mind Products, http://mindprod.com";

    /**
     * when this version was released
     */
    @SuppressWarnings( { "UnusedDeclaration" } )
    private static final String RELEASE_DATE = "2017-03-30";

    /**
     * title of Applet
     */
    @SuppressWarnings( { "UnusedDeclaration" } )
    private static final String TITLE_STRING = "Batch PAD Submitter";

    /**
     * embedded version string
     */
    @SuppressWarnings( { "UnusedDeclaration" } )
    private static final String VERSION_STRING = "26.3";

    /**
     * warn of an error
     *
     * @param errorMessage error message
     */
    private static void error( String errorMessage )
        {
        err.println( "<><><>Error<><><> " + errorMessage );
        err.println( "                  Submission bypassed." );
        err.println();
        Toolkit.getDefaultToolkit().beep();
        }

    /**
     * Get sites to submit to.
     *
     * @param filename name of text file containing list of site names.
     *
     * @return sites to submit to
     * @throws java.io.IOException if trouble reading
     */
    private static SubmissionSite[] getSubmissionSites( String filename ) throws IOException
        {
        ArrayList<SubmissionSite> list = new ArrayList<>( SubmissionSite.values().length );
        // O P E N
        FileReader fr = new FileReader( filename );
        BufferedReader br = new BufferedReader( fr, 4 * 1024 );
        while ( true )
            {
            // R E A D
            // line == null means EOF
            String siteToSubmitTo = br.readLine();
            if ( siteToSubmitTo == null )
                {
                break;
                }
            siteToSubmitTo = siteToSubmitTo.trim();
            // ignore blank lines or lines starting with ;
            if ( siteToSubmitTo.length() == 0
                 || siteToSubmitTo.startsWith( ";" ) )
                {
                continue;
                }
            // convert to enum constant, look for name not ENUM constant name.
            boolean found = false;
            for ( SubmissionSite site : SubmissionSite.values() )
                {
                if ( site.getName().equalsIgnoreCase( siteToSubmitTo ) )
                    {
                    list.add( site );
                    found = true;
                    break;
                    }
                } // end inner for
            if ( !found )
                {
                err.println( "unknown site: [" + siteToSubmitTo + "] ignored" );
                }
            } // end outer for
        br.close();
        return list.toArray( new SubmissionSite[ list.size() ] );
        }

    /**
     * is the PAD we are considering submitting valid?
     *
     * @param fullPADURLString PAD URL with http://
     *
     * @return true if PAD is valid
     */
    private static boolean isPADValid( String fullPADURLString )
        {
        if ( !fullPADURLString.startsWith( "http://" ) )
            {
            error( "The PAD URL ["
                   + fullPADURLString
                   + "] must begin with http://" );
            return false;
            }
        if ( !fullPADURLString.endsWith( ".xml" ) )
            {
            error( "The PAD URL ["
                   + fullPADURLString
                   + "] must end with .xml" );
            return false;
            }
        if ( fullPADURLString.indexOf( '\\' ) >= 0 )
            {
            error( "The PAD URL ["
                   + fullPADURLString
                   + "] must not contain any \\ characters; use / instead." );
            return false;
            }
        try
            {
            final URL url = new URL( fullPADURLString );
            final Post post = new Post();
            final String padText = post.send( url, Post.UTF8 );
            // download PAD to make sure it exists
            final int padResponseCode = post.getResponseCode();
            // later could check fields in the PAD document
            if ( !post.isGood() || padText == null || padText.length() == 0 )
                {
                error( "The PAD must already be uploaded to your website." );
                return false;
                }
            if ( padText.length() < 5000 )
                {
                error( "The uploaded PAD xml file should be 5000+ character long. It is only "
                       + padText.length()
                       + "." );
                return false;
                }
            // Later could check fields in the PAD document
            // or extract field to use is submitting to trickier sites.
            // don't check if already submitted.
            // passed all tests, let it go
            return true;
            }
        catch ( MalformedURLException e )
            {
            error( "Your PAD URL [\"+  fullPADURLString + \"] is malformed." );
            return false;
            }
        }

    /**
     * @param fullPADURLString URL of pad we are submitting
     * @param siteName         name of site we are submitting to
     *
     * @return place to put HTML output for this submission
     * @throws java.io.FileNotFoundException if trouble writing.
     */
    private static BufferedWriter openLog( String fullPADURLString, String siteName ) throws FileNotFoundException
        {
        // log to a file of the form quoter_FileDownload.log.html
        // http://mindprod.com/pad/quoter.xml --> quoter
        String padName;
        if ( fullPADURLString.length() < 4 )
            {
            padName = "unknown";
            }
        else
            {
            padName = fullPADURLString.substring( 0, fullPADURLString.length() - 4 );
            int place = padName.lastIndexOf( "/" );
            padName = padName.substring( place + 1 );
            }
        // O P E N
        final FileOutputStream fos = new FileOutputStream( padName + "_" + siteName + ".log.html", false /* append */ );
        final OutputStreamWriter osw = new OutputStreamWriter( fos );
        return new BufferedWriter( osw, 20000/* buffsize in chars */ );
        }

    /**
     * main, takes name of list of PADs from command line. and submits all those PADs to various websites. invoked with
     * java.exe -jar submitbatch.jar somepads.list somesites.list
     *
     * @param args one or two args, name of file containing pad urls, name of sites.
     */
    public static void main( String[] args )
        {
        try
            {
            if ( !( 1 <= args.length && args.length <= 2 ) )
                {
                err.println(
                        "SubmitBatch command line must have a the name of a file containing the PAD URLs to submit,\n" +
                        "e.g. somepads.list and optionally the name of a file containing the URLs of the sites to " +
                        "submit to,\n" +
                        "e.g. somesites.list."
                );
                System.exit( 1 );
                }
            if ( args[ 0 ].startsWith( "http:" ) )
                {
                err.println(
                        "SubmitBatch command line must have filename of list of PAD URLs, e.g. pads.list, " +
                        "not an URL."
                );
                System.exit( 1 );
                }
            final SubmissionSite[] sitesToSubmitTo;
            if ( args.length == 2 )
                {
                sitesToSubmitTo = getSubmissionSites( args[ 1 ] );
                }
            else
                {
                // submit to everything
                sitesToSubmitTo = SubmissionSite.values();
                }
            // O P E N
            FileReader fr = new FileReader( args[ 0 ] );
            BufferedReader br = new BufferedReader( fr, 4 * 1024/* buffsize */ );
            while ( true )
                {
                // R E A D
                // line == null means EOF
                String fullPADURLString = br.readLine();
                if ( fullPADURLString == null )
                    {
                    break;
                    }
                fullPADURLString = fullPADURLString.trim();
                // ignore blank lines or lines starting with ;
                if ( fullPADURLString.length() == 0
                     || fullPADURLString.startsWith( ";" ) )
                    {
                    continue;
                    }
                out.println( "" );
                out.println( "-------------------------------" );
                out.println( "" );
                out.println( ">>>> SUBMITTING " + fullPADURLString );
                out.println( "" );
                if ( isPADValid( fullPADURLString ) )
                    {
                    // submit PAD to each site.
                    for ( SubmissionSite site : sitesToSubmitTo )
                        {
                        //                        out.println( "Submitting "
                        //                                     + fullPADURLString
                        //                                     + " to "
                        //                                     + site.getName() );
                        String siteResponse = site.submit( fullPADURLString );
                        if ( siteResponse == null )
                            {
                            siteResponse = "[no response]";
                            }
                        out.println( "Response from: "
                                     + site.getName()
                                     + " >>>"
                                     + SubmissionSite.getResponseCode()
                                     + "<<< "
                                     + SubmissionSite.getResponseMessage() + "\n" );
                        BufferedWriter log = openLog( fullPADURLString, site.getName() );
                        log.write( siteResponse );
                        log.close();
                        } // end for
                    } // end if
                } // end while
            // C L O S E
            br.close();
            out.println( "DONE" );
            }
        catch ( IOException e )
            {
            err.println();
            e.printStackTrace( err );
            err.println();
            System.exit( 1 );
            }
        }
    }
