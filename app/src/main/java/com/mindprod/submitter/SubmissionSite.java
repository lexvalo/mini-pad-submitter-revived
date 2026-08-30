/*
 * [SubmissionSite.java]
 *
 * Summary: Enumeration of all the websites we submit PADs to and the parameters we must include.
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
 *                 Seek4Software, SharewarePower, Soft-db, Softake, Softholm, SoftLookup
 *  4.4 2009-05-11 remove DL Daddy, add Softwarelode, Digimodes, Download-by
 *  4.5 2009-05-19 remove TrialFiles, add Publish-Me, AlphaDownloads , DownloadChoice, SoftwareArchiveIsGreat,
 *                 Download5000, DownloadArsivi, DownloadShareware, DownloadStation,
 *                 EliasSoftDownloads, FreeSoftwareSharewareDownloads,
 *                 FreeShareWeb, FreewareArchiv, Freeware1,
 *                 FreeSoftwareApps, Goooggle, SafeFreeDownloads, SafeFreeSoftware, SafeFreeSoftwarmk4
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
 *  5.4 2009-12-02 remove FileVolution, add Download11, SoftCab, AfDown, DeltaLoad, DesktopShareware, 12buzz,
 *                 GetSharewareForFree, FreePadDatabase, FreewareTown
 *  5.5 2009-12-11 add 11 sites: DownloadDir, EuSoftNet, FreeSoftwarePrograms, IMfreeware, dlTube,
 *                 MySoftwareList, NewDownload, SoftwareCrown, PeterBurgess, SearchSomeSoft, SearchAllSoft
 *  5.6 2009-12-13 add 10 sites: 4software2Download, SharewareCheap, SoftwareRatings,
 *                 ShopLagom, SmallFreeware, Soft4Sale, SoftMobile, SoftwareMatrix, SystemUtils, WorldSoftwareArchive
 *  5.7 2009-12-17 add 16 sites: 123FreeSoft, 4Software2Download, DownloadYourSoftware, EzySoft, FastShareware,
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
 *  8.0 2010-08-09 add Ware23, ZoomLoad.
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
 * 12.0 2011-08-30 update access technique for ActiveMerge, AbsolutelyFreeSoftware, AsfConverter, FreewareSoftwareLinks
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
 * 15.1 2012-02-29 drop 15 sites: Download5000, WebbyTools, FreeSoft, iPadSoftOnline, iPodTouchtoComputer,
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
 * 18.2 2012-10-31 add ComAtoZ, FreeSoftwareDownloads, FreeSoftwareSharewareDownloads.
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
 * 19.8 2013-03-08 drop softwaredownloads.me, add , MacSoftware911, HotCart, Linux112,
 *                 Windows8Downloads
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
 * 21.3 2013-08-08 drop uniqueidea.net
 * 21.4 2013-08-23 drop bg-soft.net/, newsoft4you , Searchsoftwar.com, www.winload.
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
 * 22.7 2014-06-12 add CoolTechsOftWare, add AppVisor category.
 * 22.8 2014-06-24 drop 5ofttares.com.
 * 22.9 2014-07-01 drop http://www.pocket-pc-freeware.com/
 * 23.0 2014-08-16 add TestBD, BgSoft, AllSoftWares, FileLook,
 *                 SpacyHost, WebContentSolutions, SoftMerge,
 *                 Business112, Linux112, MacSoftware911, Soft112, TrinityFiles,
 *                 Apps112
 *                 drop DownloadFreePrograms
 * 23.1 2014-08-27 drop sharewarebay, pocketpc-software-downlsoads
 * 23.2 2014-09-06 drop dltube, add MatchGameR, DownloadFreeProgram
 * 23.3 2014-09-23 drop 3area.com lqko.com apps112
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
 * 24.3 2015-08-21 drop filelook, liotron, getallsoft, WebZf, downloadarsivi
 * 24.4 2015-10-18 drop softcrave appspalette savefile add bigdreamsoft freedownloadssclub
 * 24.5 2015-11-29 drop mvbbb, sharewarecheap, ware23, sharewareisland
 * 24.6 2015-12-24 drop bestsoftwarefordownload, bestvistadownloads, freedownloadsclub, top4download, windows7download, windows8downloads
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

import com.mindprod.common18.Build;
import com.mindprod.common18.EIO;
import com.mindprod.http.Get;
import com.mindprod.http.Post;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static java.lang.System.*;
/* A D D I N G   A   N E W   W E B S I T E
 * Adding a new website is quite a production:
 * 01. crack the site with Crack or Wireshark.
 * 02. add submitViaPost or submitViaGet code to the SubmissionSiteEnum.
 * 03. add a line to com\mindprod\submitter\nohassle.csv
 * 04. create an icon for the site, decorate it with FastStone edge and store it in mindprod\image\padvendors\
 * 05. add to both version comments in SubmissionSite, Submitter, SubmitBatch
 * 06. update VERSION_STRING and RELEASE_DATE in Submitter and SubmitBatch.
 * 07. run SubmissionSite to regenerate allsites.list. Fix ordering errors.
 * 08. insert numbered list of submission sites output of SubmissionSite into use.txt
 * 09. run Funduc search and replace to globally correct the number of websites with fixsubmitcount.srs script.
 * 10. use PADGen to update the PAD file, version, date, name of zip distributable.
 * 11. run com\mindprod\submitter\tidy.btm to tidy the *.csv file
 * 12. manually adjust the various com\mindprod\submitter\*.list files that your *.csv and *.list files are consistent.
 * 13. Run Consistency.
 * 14. modify version number in com.mindprod.zzz.DefineAllProjects for Submitters.
 * 15. Regenerate ant build with zzz.  Will get some errors.
 * 16. use FROMI to copy changed files from E:\intellij to E:\com\mindprod
 * 17. run cd \com\mindprod\submitter then ant postzip to recompile, build jar, zip distributable
 * 18. run prices to update the version and date of submitter distribution.
 * 19. Use e.bat to invoke HtmlMacros to expand macros on mindprod website so products, manual,
 * hassle and nohassle HTML current.
 * 20. With a browser make sure the following pages look correct. mindprod\jgloss\nohassle.html mindprod
 * .\applet\submitter.html, mindprod\jgloss\hassle.html.
 * 21  Especially check the number of sites, and appearance of new icon.
 * 22. Regenerate ant build with zzz. Should not get any errors. Will catch files missing from zip.
 * 23. run DESC in com\mindprod\submitter to auto-describe files.
 * 24. run DES in com\mindprod\submitter to name stragglers.
 * 25. run ant clean postzip jet to recompile, build jar, zip distributable and local jet exe with latest HTML
 * expansions.
 * 26. regenerate a new screenshot with new version #
 * 27. test new site(s) with some.bat/SubmitBatch. Delete logs. Delete grabasp.csv and its *.html files.
 * 28. check *.look that all files expected there, and no extras.
 * 29. distribute.
 * 30. update PAD at appvisor with new count and version.
 */
/* D R O P P I N G   A   W E B S I T E
 * 01. note sites to drop in the SubmissionSite comments in SubmissionSite, SubmitBatch, Submitter.
 * 02. drop SubmissionSite code.
 * 03. move line in com\mindprod\submitter\nohassle.csv to com\mindprod\submitter\dead.csv, marking Suspended etc.
 * 04. delete icon from mindprod\image\padvendor\
 * 05. update VERSION_STRING and RELEASE_DATE in Submitter and SubmitBatch.
 * 06. run com\mindprod\submitter\tidy.btm to tidy the *.csv file
 * 07. run SubmissionSite to regenerate allsites.list. Fix reported ordering errors.
 * 08. insert numbered list of submission sites output of SubmissionSite into use.txt
 * 09. run search and replace to globally correct the number of websites with submitCount.srx script.
 * 10. manually adjust the various com\mindprod\submitter\*.list files
 * 11. run detectduppadsites.exe to make sure files are consistent, that your *.csv and *.list files are consistent.
 * 12. use GRAB to copy changed files from E:\intellij to E:\com\mindprod\submitter
 * 13. modify version number in com.mindprod.zzz.DefineAllProjects for Submitters.
 * 14. Regenerate ant build with zzz.  Will get some errors.
 * 15. cd E:\com\mindprod\submitter and use GRAB to copy changed files from E:\intellij to E:\com\mindprod
 * 16. run price to update the version and date of submitter distribution.
 *     mindprod\applet\submitter.html, mindprod\jgloss\hassle.html.
 * 17. With a browser make sure the following pages look correct. mindprod\jgloss\nohassle.html
 * 18. run DESC in com\mindprod\submitter to auto-describe files.
 * 19. run DES in com\mindprod\submitter to name stragglers.
 * 20. Regenerate ant build with zzz with new version number. Should not get any errors. Will catch files missing
 * from zip.
 * 21. run ant clean postzip jet to recompile, build jar, zip distributable and local jet exe with latest HTML
 * 22. regenerate a new screenshot with new version #
 * 23. run ant postzip jet to recompile, build jar, zip distributable and local jet exe with latest HTML
 * expansions and get rid of class files for deleted sites.
 * 23. test. delete logs.
 * 24. distribute/upload.
 * 25. Use Appvisor to update the PAD file, version, date, name of zip distributable, number of padsites
 */

/**
 * Enumeration of all the websites we submit PADs to and the parameters we must include.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 26.3 2017-03-30 drop http://paulspicks.com/
 * @since 2007
 */
enum SubmissionSite
    {
        X2SOFTWARE( "2Software", "http://www.2-software.net/submit.html", "/submit.html" )
                    {
                    /**
                     * Simulate manual submit
                     * @param pad URL of the pad xml file we are submitting.
                     */
                    String submit( String pad )
                        {
                        return submitViaPost(
                                "pad_file_url", pad,
                                "pad_submitted", "Submit PAD File" );
                        }
                    },
        ABABA_SOFT( "AbabaSoft", "http://www.ababasoft.com/index.html", "checkadd.php" )
                    {
                    /**
                     * Simulate manual submit
                     * @param pad URL of the pad xml file we are submitting.
                     */
                    String submit( String pad )
                        {
                        return submitViaPost(
                                "padurl", pad,
                                "psubmit", "Submit PAD File"
                        );
                        }
                    },
        ABC_DATOS( "ABCDatos", "http://www.abcdatos.com/programas/padform.html", "/cgi-bin/pad.cgi" )
                    {
                    /**
                     * Simulate manual submit
                     * @param pad URL of the pad xml file we are submitting.
                     */
                    String submit( String pad )
                        {
                        return submitViaGet( "padurl", pad );
                        }
                    },
        AB_DOWNLOADS( "ABDownloads", "http://www.ab-downloads.com/english/Submit/", "/english/Submit/submit.htm" )
                    {
                    /**
                     * Simulate manual submit
                     * @param pad URL of the pad xml file we are submitting.
                     */
                    String submit( String pad )
                        {
                        return submitViaPost(
                                "padfile", pad );
                        }
                    },
        ALLSOFTWARES( "AllSoftWares", "http://windows.en.all-softwares.com/submit.php", "/submit.php" )
                    {
                    /**
                     * Simulate manual submit
                     * @param pad URL of the pad xml file we are submitting.
                     */
                    String submit( String pad )
                        {
                        return submitViaPost( "url", pad );
                        }
                    },
        APP_DOWN( "AppDown", "http://appdown.com/submit.php", "/checkadd.php" )
                    {
                    /**
                     * Simulate manual submit
                     * @param pad URL of the pad xml file we are submitting.
                     */
                    String submit( String pad )
                        {
                        return submitViaPost(
                                "padurl", pad,
                                "regNowID", "",
                                "shareItID", "",
                                "psubmit", "Submit PAD File" );
                        }
                    },
        ASKED_FILES( "AskedFiles", "http://www.askedfiles.com/submit.php", "/checkadd" )
                    {
                    /**
                     * Simulate manual submit
                     * @param pad URL of the pad xml file we are submitting.
                     */
                    String submit( String pad )
                        {
                        return submitViaPost(
                                "padurl", pad,
                                "psubmit", "Submit PAD File" );
                        }
                    },
        BIGDREAMSOFT( "BigDreamSoft", "http://www.bigdreamsoft.com/submit.php", "/checkadd.php" )
                    {
                    /**
                     * Simulate manual submit
                     * @param pad URL of the pad xml file we are submitting.
                     */
                    String submit( String pad )
                        {
                        return submitViaPost(
                                "padurl", pad,
                                "psubmit", "Submit PAD File" );
                        }
                    },
        BIZBEP( "BizPep", "http://bizpep.com/submit.php", "/add.php" )
                    {
                    /**
                     * Simulate manual submit
                     * @param pad URL of the pad xml file we are submitting.
                     */
                    String submit( String pad )
                        {
                        return submitViaPost(
                                "type", "Software",
                                "type", "Web%20Site",
                                "so", pad,
                                "review", "true",
                                "review", "",
                                "catsubmit", "Submit" );
                        }
                    },
        DE_SOFT_LIST( "DeSoftList", "http://de.softlist.ws/addsoft/", "/addsoft/" )
                    {
                    /**
                     * Simulate manual submit
                     * @param pad URL of the pad xml file we are submitting.
                     */
                    String submit( String pad )
                        {
                        return submitViaPost(
                                "pad_url", pad,
                                "sid", "-1",
                                "submit", "Submit" );
                        }
                    },
        DOWNLOAD100( "Download100", "http://www.download100.net/services/submit-soft.aspx",
                "/services/submit-soft.aspx" )
                    {
                    /**
                     * Simulate manual submit
                     * @param pad URL of the pad xml file we are submitting.
                     */
                    String submit( String pad )
                        {
                        return submitViaGet(
                                "padurl", pad );
                        }
                    },
        DOWNLOAD_11( "Download11", "http://download11.com/submit/", "/submit/submit.php" )
                    {
                    /**
                     * Simulate manual submit
                     * @param pad URL of the pad xml file we are submitting.
                     */
                    String submit( String pad )
                        {
                        return submitViaGet(
                                "url", "" );
                        }
                    },
        DOWNLOAD_3K( "Download3K", "http://www.download3k.com/submit.php", "/submit.php" )
                    {
                    /**
                     * Simulate manual submit
                     * @param pad URL of the pad xml file we are submitting.
                     */
                    String submit( String pad )
                        {
                        return submitViaPost(
                                "url", pad,
                                "id_regnow", "" );
                        }
                    },
        DOWNLOAD_3K_DE( "Download3KDe", "http://www.download3k.de/submit.php", "/submit.php" )
                    {
                    /**
                     * Simulate manual submit
                     * @param pad URL of the pad xml file we are submitting.
                     */
                    String submit( String pad )
                        {
                        return submitViaPost(
                                "url", pad,
                                "id_regnow", "" );
                        }
                    },
        DOWNLOAD_3K_ES( "Download3KEs", "http://www.download3k.es/submit.php", "/submit.php" )
                    {
                    /**
                     * Simulate manual submit
                     * @param pad URL of the pad xml file we are submitting.
                     */
                    String submit( String pad )
                        {
                        return submitViaPost(
                                "url", pad,
                                "id_regnow", "" );
                        }
                    },
        DOWNLOAD_3K_FR( "Download3KFr", "http://www.download3k.fr/submit.php", "/submit.php" )
                    {
                    /**
                     * Simulate manual submit
                     * @param pad URL of the pad xml file we are submitting.
                     */
                    String submit( String pad )
                        {
                        return submitViaPost(
                                "url", pad,
                                "id_regnow", "" );
                        }
                    },
        DOWNLOAD_3K_RO( "Download3kRo", "http://www.download3k.ro/submit.php", "/submit.php" )
                    {
                    /**
                     * Simulate manual submit
                     * @param pad URL of the pad xml file we are submitting.
                     */
                    String submit( String pad )
                        {
                        return submitViaPost(
                                "url", pad,
                                "id_regnow", "" );
                        }
                    },
        DOWNLOAD_4_YOU( "Download4You", "http://download4you.com/php/add.php", "/php/add_software.php" )
                    {
                    /**
                     * Simulate manual submit
                     * @param pad URL of the pad xml file we are submitting.
                     */
                    String submit( String pad )
                        {
                        return submitViaPost(
                                "LOAD_PAD_URL", pad,
                                "action", "loaded",
                                "submit", "Load PAD file!" );
                        }
                    },
        DOWNLOAD_BY( "DownloadBy", "http://www.download-by.net/submit.php", "/checkadd.html" )
                    {
                    /**
                     * Simulate manual submit
                     * @param pad URL of the pad xml file we are submitting.
                     */
                    String submit( String pad )
                        {
                        return submitViaPost(
                                "padurl", pad,
                                "psubmit", "Submit PAD File" );
                        }
                    },
        DOWNLOAD_O_MANIAC( "DownloadoManiac", "http://download.omani.ac/submit.php", "/submit.php" )
                    {
                    /**
                     * Simulate manual submit
                     * @param pad URL of the pad xml file we are submitting.
                     */
                    String submit( String pad )
                        {
                        return submitViaPost(
                                "pad", pad,
                                "form", "submitting"
                        );
                        }
                    },
        DOWNLOADREADY( "DownloadReady", "http://www.downloadready.com/submit.php", "/checkadd.php" )
                    {
                    /**
                     * Simulate manual submit
                     * @param pad URL of the pad xml file we are submitting.
                     */
                    String submit( String pad )
                        {
                        return submitViaPost(
                                "padurl", pad,
                                "psubmit", "Submit",
                                "preset", "Reset" );
                        }
                    },
        DOWNLOADS2( "Downloads2", "http://www.downloads2.com/submit.php", "/checkadd.php" )
                    {
                    /**
                     * Simulate manual submit
                     * @param pad URL of the pad xml file we are submitting.
                     */
                    String submit( String pad )
                        {
                        return submitViaPost(
                                "padurl", pad,
                                "psubmit", "PAD File Submit"
                        );
                        }
                    },
        DOWNLOADS_AREA( "DownloadsArea", "http://www.downloadsarea.com/Submit.html", "/Submit.html" )
                    {
                    /**
                     * Simulate manual submit
                     * @param pad URL of the pad xml file we are submitting.
                     */
                    String submit( String pad )
                        {
                        return submitViaPost(
                                "padurl", pad );
                        }
                    },
        DOWNLOAD_SPIN( "DownloadSpin", "http://www.downloadspin.com/submit.php", "/checkadd.php" )
                    {
                    /**
                     * Simulate manual submit
                     * @param pad URL of the pad xml file we are submitting.
                     */
                    String submit( String pad )
                        {
                        return submitViaPost( "padurl", pad,
                                "psubmit", "Submit PAD File" );
                        }
                    },
        DOWNLOAD_TYPHOON( "DownloadTyphoon", "http://www.downloadtyphoon.com/submit-pad-file", "/submit-pad-file.html" )
                    {
                    /**
                     * Simulate manual submit
                     * @param pad URL of the pad xml file we are submitting.
                     */
                    String submit( String pad )
                        {
                        return submitViaGet(
                                "padurl", pad,
                                "regnow", "" );
                        }
                    },
        EURO_DOWNLOAD( "EuroDownload", "http://www.eurodownload.com/pad_submit.php", "/pad_check.php" )
                    {
                    /**
                     * Simulate manual submit
                     * @param pad URL of the pad xml file we are submitting.
                     */
                    String submit( String pad )
                        {
                        return submitViaPost(
                                "padurl", pad,
                                "regnow", "",
                                "sellshareware", "",
                                "esellerate", "",
                                "shareit", "",
                                "bmtmicro", "",
                                "plimus", "",
                                "psubmit", "Submit PAD File" );
                        }
                    },
        EVOCERO( "Evocero", "http://www.evocero.com/evosoftware/submit.php", "/evosoftware/checkaddp.php" )
                    {
                    /**
                     * Simulate manual submit
                     * @param pad URL of the pad xml file we are submitting.
                     */
                    String submit( String pad )
                        {
                        return submitViaPost(
                                "padurl", pad,
                                "psubmit", "Submit PAD File" );
                        }
                    },
        FAST_SHAREWARE( "FastShareware", "http://www.fastshareware.com/submit.php", "/addpad.php" )
                    {
                    /**
                     * Simulate manual submit
                     * @param pad URL of the pad xml file we are submitting.
                     */
                    String submit( String pad )
                        {
                        return submitViaPost(
                                "padurl", pad,
                                "psubmit", "Submit PAD File" );
                        }
                    },
        FD4A( "Fd4a", "http://www.fd4a.net/submit.php", "/submit.php" )
                    {
                    /**
                     * Simulate manual submit
                     * @param pad URL of the pad xml file we are submitting.
                     */
                    String submit( String pad )
                        {
                        return submitViaPost(
                                "padurl", pad );
                        }
                    },
        FILE_CLUSTER( "FileCluster", "http://www.filecluster.com/submit-software.html", "/submit-software.html" )
                    {
                    /**
                     * Simulate manual submit
                     * @param pad URL of the pad xml file we are submitting.
                     */
                    String submit( String pad )
                        {
                        return submitViaPost(
                                "padurl", pad,
                                "action", "ADD"
                        );
                        }
                    },
        FILE_DIR( "FileDir", "http://filedir.com/submit/form/", "/submit/form/" )
                    {
                    /**
                     * Simulate manual submit
                     * @param pad URL of the pad xml file we are submitting.
                     */
                    String submit( String pad )
                        {
                        return submitViaPost(
                                "act", "submit_do",
                                "pad", pad,
                                "Submit", "Submit" );
                        }
                    },
        FILE_PROFILE( "FileProfile", "http://www.fileprofile.com/submit.htm", "/submitdone.php" )
                    {
                    /**
                     * Simulate manual submit
                     * @param pad URL of the pad xml file we are submitting.
                     */
                    String submit( String pad )
                        {
                        return submitViaGet( "xmlpath", pad );
                        }
                    },
        FILES_GUARD( "FilesGuard", "http://filesguard.com/submit.php", "/checkadd.php" )
                    {
                    /**
                     * Simulate manual submit
                     * @param pad URL of the pad xml file we are submitting.
                     */
                    String submit( String pad )
                        {
                        return submitViaPost(
                                "padurl", pad,
                                "psubmit", "Submit PAD File" );
                        }
                    },
        FIND_SOFT( "FindSoftNet", "http://www.findsoft.net/Submit.html", "/" )
                    {
                    /**
                     * Simulate manual submit
                     * @param pad URL of the pad xml file we are submitting.
                     */
                    String submit( String pad )
                        {
                        return submitViaPost(
                                "padurl", pad,
                                "psubmit", "Submit" );
                        }
                    },
        FIND_SOFTWARE_EU( "FindSoftwareeu", "http://www.findsoftware.eu/submit.php", "/submit" )
                    {
                    /**
                     * Simulate manual submit
                     * @param pad URL of the pad xml file we are submitting.
                     */
                    String submit( String pad )
                        {
                        return submitViaPost(
                                "pad_url", pad );
                        }
                    },
        FOR_MAC( "ForMac", "http://for-mac.com/submit.html", "/index" )
                    {
                    /**
                     * Simulate manual submit
                     * @param pad URL of the pad xml file we are submitting.
                     */
                    String submit( String pad )
                        {
                        return submitViaGet(
                                "padurl", pad,
                                "psubmit", "Submit PAD File" );
                        }
                    },
        FREE_SAFE_SOFT( "FreeSafeSoft", "http://freesafesoft.com/submit.php", "/checkadd.php" )
                    {
                    /**
                     * Simulate manual submit
                     * @param pad URL of the pad xml file we are submitting.
                     */
                    String submit( String pad )
                        {
                        return submitViaPost(
                                "padurl", pad,
                                "userregnowpid", "",
                                "psubmit", "Submit PAD File" );
                        }
                    },
        FREESOFTWARESHAREWAREDOWNLOADS( "FreeSoftwareSharewareDownloads", "http://free-software-shareware-downloads" +
                                                                          ".com/submit.php", "/checkadd.php" )
                    {
                    /**
                     * Simulate manual submit
                     * @param pad URL of the pad xml file we are submitting.
                     */
                    String submit( String pad )
                        {
                        return submitViaPost(
                                "padurl", pad,
                                "psubmit", "Submit PAD File" );
                        }
                    },
        FREEWARE1( "Freeware1", "http://www.freeware1.com/submit.php", "/checkadd.php" )
                    {
                    /**
                     * Simulate manual submit
                     * @param pad URL of the pad xml file we are submitting.
                     */
                    String submit( String pad )
                        {
                        return submitViaPost(
                                "padurl", pad,
                                "psubmit", "Submit PAD File" );
                        }
                    },
        FREEWARES_ZDOWN( "FreewaresZDown", "http://www.freewares.z-down.com/submit-freeware.php", "/checkadd.php" )
                    {
                    /**
                     * Simulate manual submit
                     * @param pad URL of the pad xml file we are submitting.
                     */
                    String submit( String pad )
                        {
                        return submitViaPost(
                                "padurl", pad,
                                "regnow", "",
                                "psubmit", "Submit PAD File" );
                        }
                    },
        IM_FREEWARE( "ImFreeware", "http://www.imfreeware.com/submitres", "/submitres" )
                    {
                    /**
                     * Simulate manual submit
                     * @param pad URL of the pad xml file we are submitting.
                     */
                    String submit( String pad )
                        {
                        return submitViaPost(
                                "padurl", pad,
                                "submittype", "software",
                                "submit", "Submit" );
                        }
                    },
        MY_DOWNLOAD_PLANET( "MyDownloadPlanet", "http://www.mydownloadplanet.com/submit-software.aspx",
                "/submit-software.aspx" )
                    {
                    /**
                     * Simulate manual submit
                     * @param pad URL of the pad xml file we are submitting.
                     */
                    String submit( String pad )
                        {
                        return submitViaPost(
                                "pad", pad,
                                "Submit", "Submit" );
                        }
                    },
        PLANETSOFTS( "PlanetSofts", "http://www.planetsofts.com/submit.html", "/submit.html" )
                    {
                    /**
                     * Simulate manual submit
                     * @param pad URL of the pad xml file we are submitting.
                     */
                    String submit( String pad )
                        {
                        return submitViaPost(
                                "performAction", "1",
                                "rss", pad );
                        }
                    },
        RARITY_SOFT( "RaritySoft", "http://www.raritysoft.com/pad/submit", "/pad/submit" )
                    {
                    /**
                     * Simulate manual submit
                     * @param pad URL of the pad xml file we are submitting.
                     */
                    String submit( String pad )
                        {
                        return submitViaPost( "SubmitPadFileForm[PadFileUrl]", pad );
                        }
                    },
        RECOVERY_REVIEW( "RecoveryReview", "http://recovery-review.com/pad-submission.html", "/index.php" )
                    {
                    /**
                     * Simulate manual submit
                     * @param pad URL of the pad xml file we are submitting.
                     */
                    String submit( String pad )
                        {
                        final Post post = new Post();
                        // has both get and post style parms
                        post.setParms( "action", "submit" );
                        post.setPostParms( "padurl", pad );
                        final URL actionURL = getActionURL();
                        final String result = post.send( actionURL.getHost(), -1, actionURL.getPath(), Post.UTF8 );
                        responseCode = post.getResponseCode();
                        responseMessage = post.getResponseMessage();
                        return result;
                        }
                    },
        RU_SOFT_LIST_RU( "RuSoftList", "http://ru.softlist.ws/addsoft/", "/addsoft/" )
                    {
                    /**
                     * Simulate manual submit
                     * @param pad URL of the pad xml file we are submitting.
                     */
                    String submit( String pad )
                        {
                        return submitViaPost(
                                "pad_url", pad,
                                "sid", "-1",
                                "submit", "Submit" );
                        }
                    },
        SHAREWARE_VILLE( "Sharewareville", "http://sharewareville.com/submit.php", "/submit_confirm.php" )
                    {
                    /**
                     * Simulate manual submit
                     * @param pad URL of the pad xml file we are submitting.
                     */
                    String submit( String pad )
                        {
                        return submitViaPost(
                                "padurl", pad,
                                "psubmit", "Submit PAD File" );
                        }
                    },
        SOFT112( "Soft112", "http://www.soft112.com/submit-software.html", "/submit-software.html" )
                    {
                    /**
                     * Simulate manual submit
                     * @param pad URL of the pad xml file we are submitting.
                     */
                    String submit( String pad )
                        {
                        return submitViaPost(
                                "csrf", "6793a9ec8f50edebf791e8d5f015aadc",
                                "submit_pad[ip]", "184.66.171.186",
                                "submit_pad[url]", pad );
                        }
                    },
        SOFT_321( "Soft321", "http://www.soft321.com/submit.php", "/checkadd.php" )
                    {
                    /**
                     * Simulate manual submit
                     * @param pad URL of the pad xml file we are submitting.
                     */
                    String submit( String pad )
                        {
                        return submitViaPost(
                                "padurl", pad,
                                "linkbackurl", "",  // optional
                                "psubmit", "Submit PAD File" );
                        }
                    },
        SOFT_FOLDER( "SoftFolder", "http://www.softfolder.com/software/submit.php", "/software/checkadd.php" )
                    {
                    /**
                     * Simulate manual submit
                     * @param pad URL of the pad xml file we are submitting.
                     */
                    String submit( String pad )
                        {
                        return submitViaPost(
                                "padurl", pad,
                                "psubmit", "Submit PAD File" );
                        }
                    },
        SOFT_HOLM( "Softholm", "http://www.softholm.com/php/pad_submit.html", "/php/pads.php" )
                    {
                    /**
                     * Simulate manual submit
                     * @param pad URL of the pad xml file we are submitting.
                     */
                    String submit( String pad )
                        {
                        return submitViaPost(
                                "url", pad );
                        }
                    },
        SOFT_LIST_WS( "SoftListWs", "http://softlist.ws/addsoft/", "/addsoft/" )
                    {
                    /**
                     * Simulate manual submit
                     * @param pad URL of the pad xml file we are submitting.
                     */
                    String submit( String pad )
                        {
                        return submitViaPost(
                                "pad_url", pad,
                                "sid", "-1",
                                "submit", "Submit" );
                        }
                    },
        SOFTWARE_BEE( "SoftwareBee", "http://www.softwarebee.com/submit.html", "/submit.html" )
                    {
                    /**
                     * Simulate manual submit
                     * @param pad URL of the pad xml file we are submitting.
                     */
                    String submit( String pad )
                        {
                        return submitViaPost(
                                "pad_file_box", pad );
                        }
                    },
        SOFTWARE_KB( "SoftwareKB", "http://www.softwarekb.com/submissions-2/submit-pad-4", "/submissions-2/submit-pad-4" )
                    {
                    /**
                     * Simulate manual submit
                     * @param pad URL of the pad xml file we are submitting.
                     */
                    String submit( String pad )
                        {
                        return submitViaPost(
                                "padfile", pad );
                        }
                    },
        SOFTWARE_WAGON( "SoftwareWagon", "http://softwarewagon.com/submit-software.php", "/submit-software.php" )
                    {
                    /**
                     * Simulate manual submit
                     * @param pad URL of the pad xml file we are submitting.
                     */
                    String submit( String pad )
                        {
                        return submitViaPost(
                                "submit_soft", "1",
                                "hlc", "",
                                "pad_url", pad,
                                "regnow_id", "" );
                        }
                    },
        SPOTPIG( "SpotPig", "http://www.spotpig.com/submit.php", "/submit.php" )
                    {
                    /**
                     * Simulate manual submit
                     * @param pad URL of the pad xml file we are submitting.
                     */
                    String submit( String pad )
                        {
                        return submitViaPost(
                                "url", pad,
                                "B1", "Submit" );
                        }
                    },
        STANDALONEINSTALLER( "StandaloneInstaller", "http://standaloneinstaller.com/submit.php", "/submit.php" )
                    {
                    /**
                     * Simulate manual submit
                     * @param pad URL of the pad xml file we are submitting.
                     */
                    String submit( String pad )
                        {
                        return submitViaPost(
                                "email", "",
                                "pad", pad );
                        }
                    },
        STYLE_XP_3X( "StyleXP3x", "http://stylexp.3x.ro/submit.php", "/checkadd.php" )
                    {
                    /**
                     * Simulate manual submit
                     * @param pad URL of the pad xml file we are submitting.
                     */
                    String submit( String pad )
                        {
                        return submitViaPost(
                                "padurl", pad,
                                "psubmit", "Submit PAD File" );
                        }
                    },
        SWDB( "Swdb", "http://swdb.ru/submit/", "/submit/" )
                    {
                    /**
                     * Simulate manual submit
                     * @param pad URL of the pad xml file we are submitting.
                     */
                    String submit( String pad )
                        {
                        return submitViaPost(
                                "padurl", pad );
                        }
                    },
        TELECHARGERZ( "TelechargerZ", "http://telecharger.z-down.com/soumission_logiciel_shareware_freeware.php",
                "/checkadd.php" )
                    {
                    /**
                     * Simulate manual submit
                     * @param pad URL of the pad xml file we are submitting.
                     */
                    String submit( String pad )
                        {
                        return submitViaPost(
                                "padurl", pad,
                                "regnow", "",
                                "psubmit", "Soumettre le fichier PAD" );
                        }
                    },
        TERABYTE( "TeraByte",
                "http://ip-66-51-104-19.tera-byte.com/submit.php",
                "/checkadd.php" )
                    {
                    /**
                     * Simulate manual submit
                     * @param pad URL of the pad xml file we are submitting.
                     */
                    String submit( String pad )
                        {
                        return submitViaPost(
                                "padurl", pad,
                                "psubmit", "Submit PAD FileHTTP" );
                        }
                    },
        TWO_BROTHERS( "TwoBrothers", "http://shareware.twobrotherssoftware.com/submit.php", "/checkadd.php" )
                    {
                    /**
                     * Simulate manual submit
                     * @param pad URL of the pad xml file we are submitting.
                     */
                    String submit( String pad )
                        {
                        return submitViaPost(
                                "padurl", pad,
                                "psubmit", "Submit PAD File" );
                        }
                    },
        WEBCONTENTSOLUTIONS( "WebContentSolutions", "http://www.webcontentsolutions.com/submitform.html", "/submitsoft.asp" )
                    {
                    /**
                     * Simulate manual submit
                     * @param pad URL of the pad xml file we are submitting.
                     */
                    String submit( String pad )
                        {
                        return submitViaPost(
                                "padurl", pad,
                                "Submit", "Submit" );
                        }
                    },
        WINDOWS10COMPATIBLE( "Windows10Compatible", "http://www.windows10compatible.com/submit", "/submit" )
                    {
                    /**
                     * Simulate manual submit
                     * @param pad URL of the pad xml file we are submitting.
                     */
                    String submit( String pad )
                        {
                        return submitViaPost(
                                "_token", "vjrpiDngMoCEwYVAD7iANyfv4OU7HfKz2ka6X9Tr",
                                "web", pad );
                        }
                    },
        YANKEE_DOWNLOAD( "YankeeDownload", "http://www.yankeedownload.com/pad_submit.php", "/pad_check.php" )
                    {
                    /**
                     * Simulate manual submit
                     * @param pad URL of the pad xml file we are submitting.
                     */
                    String submit( String pad )
                        {
                        return submitViaPost(
                                "padurl", pad,
                                "regnow", "",
                                "shareit", "",
                                "sellshareware", "",
                                "esellerate", "",
                                "bmtmicro", "",
                                "plimus", "",
                                "psubmit", "Submit PAD File" );
                        }
                    },
        ZDOWN( "ZDown", "http://www.z-down.com/submit-softwares.php", "/checkadd.php" )
                    {
                    /**
                     Simulate manual submit
                     * @param pad URL of the pad xml file we are submitting.
                     */
                    String submit( String pad )
                        {
                        return submitViaPost(
                                "padurl", pad,
                                "regnow", "",
                                "psubmit", "Submit PAD File"
                        );
                        }
                    };

    /**
     * true for extra debug output
     */
    private static final boolean DEBUGGING = false;

    /**
     * Allow 40 seconds to connect, measured in millis.
     */
    private static final int CONNECT_TIMEOUT = ( int ) TimeUnit.SECONDS.toMillis( 40 );

    /**
     * Allow 20 seconds for a read to go without progress, measured in millis, no big deal if lose tail end of response.
     */
    private static final int READ_TIMEOUT = ( int ) TimeUnit.SECONDS.toMillis( 20 );

    /**
     * responseCode of last post
     */
    private static int responseCode;

    /**
     * response message of last post
     */
    private static String responseMessage;

    /**
     * action.  If has lead / means absolute, relative to web root, without means relative to subbit directory.
     */
    private final String action;

    /**
     * name of this site for humans
     */
    private final String name;

    /**
     * URL String that where you would manually make a submission
     */
    private final URL manualSubmissionURL;

    /**
     * constructor
     *
     * @param name                name of this site for humans
     * @param manualSubmissionURL http: url of web page where you manually submit
     * @param action              form action. Lead slash mean absolute. Without means relative to submission page.
     */
    SubmissionSite( String name, String manualSubmissionURL, String action )
        {
        assert name != null : "SubmissionSite constructor requires a non-null name.";
        assert manualSubmissionURL != null : "SubmissionSite constructor requires a non-null manualSubmissionURL.";
        this.name = name;
        try
            {
            this.manualSubmissionURL = new URL( manualSubmissionURL );
            }
        catch ( MalformedURLException e )
            {
            throw new IllegalArgumentException( "Program bug: Invalid manualSubmissionURL: " + manualSubmissionURL );
            }
        this.action = action;
        }

    /**
     * Get the document base to use in evaluating relative URLs in the responses from this site
     * to help in rendering the HTML.
     *
     * @return base URL
     */
    URL getActionURL()
        {
        try
            {
            // override tail end with action, either absolute or relative
            return new URL( manualSubmissionURL, action );
            }
        catch ( MalformedURLException e )
            {
            throw new IllegalArgumentException( "Program bug: Invalid site action URL " + this.toString() );
            }
        }

    /**
     * Get the document base to use in evaluating relative URLs in the responses from this site
     * to help in rendering the HTML.
     *
     * @return base URL
     */
    URL getBaseURL()
        {
        try
            {
            // over ride manual submission html file with dummy.txt
            final String dummyURL = new URL( manualSubmissionURL, "dummy.txt" ).toString();
            // chop dummy.txt off the end
            return new URL( dummyURL.substring( 0, dummyURL.length() - "dummy.txt".length() ) );
            }
        catch ( MalformedURLException e )
            {
            throw new IllegalArgumentException( "Program bug: Invalid site base " + this.toString() );
            }
        }

    /**
     * get the human name of this enum
     *
     * @return name
     */
    String getName()
        {
        return this.name;
        }

    /*
     * Submit the PAD to this submission site.
     * @param pad URL of pad e.g. http://mindprod/pad/fileio.xml
     * @return what site said back, raw.
     */
    abstract String submit( String pad );

    /**
     * submit pad via GET with given parms
     *
     * @param parms parm=value pairs, including parmurl=pad
     *
     * @return text the website sent back.
     */
    String submitViaGet( String... parms )
        {
        final Get get = new Get();
        get.setReadTimeout( READ_TIMEOUT );
        get.setParms( parms );
        final URL actionURL = getActionURL();
        final String result = get.send( actionURL.getHost(), -1  /* port */, actionURL.getPath(), Get.UTF8 );
        responseCode = get.getResponseCode();
        responseMessage = get.getResponseMessage();
        return result;
        }

    /**
     * submit pad via POST with given parms
     *
     * @param postParms parm=value pairs, including parmurl=pad, not encoded.
     *
     * @return text the website sent back.
     */
    String submitViaPost( String... postParms )
        {
        final Post post = new Post();
        post.setConnectTimeout( CONNECT_TIMEOUT );
        post.setReadTimeout( READ_TIMEOUT );
        post.setPostParms( postParms );
        final URL actionURL = getActionURL();
        final String result = post.send( actionURL.getHost(), -1  /* port */, actionURL.getPath(), Post.UTF8 );
        responseCode = post.getResponseCode();
        responseMessage = post.getResponseMessage();
        return result;
        }

    /**
     * get resultCode of previous post
     *
     * @return 0..100s = ok
     */
    public static int getResponseCode()
        {
        return responseCode;
        }

    /**
     * get result status message of previous post
     *
     * @return response message
     */
    public static String getResponseMessage()
        {
        return responseMessage;
        }

    /**
     * provide a list of all the sites, both enum names and external names.
     *
     * @param args not used.
     */
    public static void main( String[] args )
        {
        if ( DEBUGGING )
            {
            out.println( "actions >>>>" );
            for ( SubmissionSite submissionSite : SubmissionSite.values() )
                {
                out.println( submissionSite.getActionURL() );
                }
            out.println( "bases >>>>" );
            for ( SubmissionSite submissionSite : SubmissionSite.values() )
                {
                out.println( submissionSite.getBaseURL() );
                }
            out.println( "site ENUMs >>>>" );
            for ( SubmissionSite submissionSite : SubmissionSite.values() )
                {
                out.println( submissionSite );
                }
            }
        int j = 0;
        final String[] names = new String[ SubmissionSite.values().length ];
        for ( SubmissionSite submissionSite : SubmissionSite.values() )
            {
            names[ j++ ] = submissionSite.name;
            }
        // check that sites are in order.
        String prev = null;
        for ( String name : names )
            {
            if ( prev != null && name.compareToIgnoreCase( prev ) <= 0 )
                {
                err.println( "sites in Java code out of case-sensitive order near " + name );
                }
            prev = name;
            }
        out.println( "numbered site names in case-sensitive alpha order >>>>" );
        Arrays.sort( names, String.CASE_INSENSITIVE_ORDER );
        int i = 1;
        for ( String name : names )
            {
            out.printf( "%3d. %s%n", i++, name );
            }
        // save allsites.list
        try
            {
            // O P E N
            final File allSitesFile = new File( Build.MINDPROD_SOURCE + "/submitter/allsites.list" );
            final PrintWriter prw = EIO.getPrintWriter( allSitesFile, 4 * 1024, EIO.UTF8 );
            // W R I T E
            for ( String name : names )
                {
                prw.println( name );
                }
            prw.close();
            }
        catch ( FileNotFoundException e )
            {
            err.println( "unable to create allsites.list" );
            }
        }
    }
