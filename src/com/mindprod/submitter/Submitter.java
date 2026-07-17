/*
 * [Submitter.java]
 *
 * Summary: Applet GUI to submit PAD files to various distribution websites.
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
 *  1.1 2007-01-01 uses separate thread to submit to improve repaint behaviour.
 *  1.2 2007-05-29 add DownBroad
 *  1.3 2007-06-02 add download3k
 *  1.4 2007-06-20 add downloadready
 *  1.5 2007-07-04 add HotLib
 *                 delete IgnoreTheLeft, modify HotLib, add BestFreewareDownload
 *  1.6 2007-07-04
 *  1.7 2007-08-12 add PadPile, remove 1st4software
 *  1.8 2007-08-24 use new HTTP library.
 *  1.9 2007-09-08 drop HotLib
 *  2.0 2007-09-15 add Trial-Files
 *  2.1 2007-09-25 add www.i-freeware-download.com
 *  2.2 2007-09-26 add software-dungeon.co.uk
 *  2.3 2007-11-30 ShareSoftware24
 *  2.4 2008-02-27 add Download-o-maniac!, list of batch sites
 *  2.5 2008-02-28 remove dead sites.
 *  2.6 2008-04-03 add build number to title
 *  2.7 2008-05-12 add FileCluster
 *  2.8 2008-07-25 convert to new Http package.
 *  2.9 2008-08-04 add SoftSea.
 *  3.0 2008-08-18 responses are now displayed more comprehensibly as formatted HTML, responses in words.
 *  3.1 2008-09-16 add Ozysoftware to list of submission sites.
 *  3.2 2008-10-14 remove Ozysoftware from list of submission sites
 *  3.3 2008-10-17 remove Techpedia from list of submission sites
 *  3.4 2008-10-19 remove Download2K from list of submission sites
 *  3.5 2009-02-20 add EnterHelp, Ozysoftware to list of submission sites
 *  3.6 2009-02-20 add PadRepository, TechWoods, WillemSoft to list of submission sites
 *  3.7 2009-02-21 remove SoftSea, add AbabaSoft, BestSoftware, HamSoftware, SoftwareHorizon
 *  3.8 2009-02-23 remove SoftwareArea51 and WillingSoftware.
 *                 Write Crack class to automatically analyse websites and write the code to access them
 *                 Review all code for all websites and update it to match the latest parameters.
 *                 add sites AmazingFile, AtomicDownload. DiamondsLastForever, Downloads2, GeneralFreeware,
 *                 Mayzer, PlusPro, ShellTips, Vonna, WebAcclaim, Z-Down
 *  3.9 2009-02-24 add Download-It-Now
 *  4.0 2009-03-14 add 20 new sites, log HTML responses to separate files.
 *  4.1 2009-03-16 refactor SubmissionSite
 *                 don't pester user with HTML rendering errors,
 *                 improve HTML rendering with setBase().
 *  4.2 2009-04-10 add AppDown, Dalexis. remove ActiveMerge
 *  4.3 2009-04-11 add AivSoft, FilesGuard, FreeFileSeek, GetAllSoft, HotFileDownload,
 *                 Seek4Software, SharewarePower, Soft-db, Softake, Softholm, SoftLookup
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
 *  5.0 2009-09-22 add five sites, and remove two
 *  5.1 2009-10-25 remove Techwoods, add SuperWebHunt, ABDownloads, Downloads2K, Soft321.
 *  5.2 2009-11-03 remove Sharewareville. Add Windows7Download, SoftwareDownloads
 *  5.3 2009-11-21 remove http://www.allapp.com/Submit-Software/
 *                 add http://www.5moons.net/submit.php
 *                 http://www.8844download.com/submit.htm
 *                 http://www.affiliate-referrals.net/submit.php (DL Daddy)
 *                 http://www.goodownload.com/submit.html
 *                 http://www.resourcefill.com/submit.php
 *                 http://www.uniqueidea.net/download/submit.asp
 *                 http://www.acidfiles.com/submit.html
 *  5.4 2009-12-02 remove FileVolution, add Download11, SoftCab, AfDown, DeltaLoad, DesktopShareware, 12buzz,
 *                 GetSharewareForeFree, FreePadDatabase, FreewareTown
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
 *  6.8 2010-03-23 drop ShellTips, Gooogle, user selectable Look and Feel.
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
 * 21.9 2014-01-07 drop ozysoftware, download-up, review-world, software-listin
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
 * 23.5 2014-11-28 drop sharewarefiles.net, cooltechsoftware.com, jqwn, qdyu, add TryBeforePay, Download3KFr, IfBit
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
 * 25.4 2016-06-23 dro hame-software and 5moons
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
import com.mindprod.common18.CMPAboutJBox;
import com.mindprod.common18.FontFactory;
import com.mindprod.common18.HybridJ;
import com.mindprod.common18.JEButton;
import com.mindprod.common18.Laf;
import com.mindprod.common18.Misc;
import com.mindprod.common18.ST;
import com.mindprod.common18.VersionCheck;
import com.mindprod.entities.DeEntifyStrings;
import com.mindprod.http.Get;

import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import static java.lang.System.*;

/**
 * Applet GUI to submit PAD files to various distribution websites.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 26.3 2017-03-30 drop http://paulspicks.com/
 * @since 2007
 */
@SuppressWarnings( { "FieldCanBeLocal" } )
public final class Submitter extends JApplet implements Runnable
    {
    /**
     * allow user to resusbumit the same url within a week.  Normally false. True during debugging.
     */
    private static final boolean PERMIT_RESUBMIT = false;

    /**
     * Applet height in pixels
     */
    private static final int APPLET_HEIGHT = 500;

    /**
     * Applet width in pixels
     */
    private static final int APPLET_WIDTH = 680;

    private static final int FIRST_COPYRIGHT_YEAR = 2007;

    /**
     * Sites to submit to, loaded once from sites.txt (next to the jar) instead of
     * being a hardcoded enum - edit that file to add, remove, or change sites.
     */
    private static final java.util.List<Site> SITES = Site.loadAll();

    /**
     * how many websites we submit to.
     */
    private static final int HOW_MANY_WEBSITES = SITES.size();

    /**
     * How long to wait for response from site to to finish rendering. Does not count time for initial response.
     * It may need time to load images, style sheets etc.
     */
    private static final int MILLIS_TO_ADMIRE = 6000;

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
     * fake pad name to use an as example, no lead /
     */
    private static final String SAMPLE_PAD_URL = "hypotheticalprogram.xml";

    /**
     * fake website URL to use an as example, no trailing /
     */
    private static final String SAMPLE_WEBSITE_URL = "http://mypretendwebsite.com/pad";

    /**
     * title of Applet
     */
    private static final String TITLE_STRING = "Mini PAD Submitter";

    /**
     * embedded version string
     */
    @SuppressWarnings( { "UnusedDeclaration" } )
    private static final String VERSION_STRING = "26.3 Revived";

    /**
     * background colour, pale green to match website
     */
    private static final Color BACKGROUND_FOR_BODY = Build.BACKGROUND_FOR_BLENDING;

    /**
     * background colour for instructions.  Default grey is too dark
     */
    private static final Color BACKGROUND_FOR_INSTRUCTIONS = new Color( 0xf8f8f8 );

    /**
     * instruction background colour when submitting
     */
    private static final Color BACKGROUND_FOR_WORKING = new Color( 0x005e6e/* dark cyan */ );

    /**
     * something went wrong colour
     */
    private static final Color FOREGROUND_FOR_ALERT = new Color( 0xdc143c/* crimson */ );

    /**
     * something went wrong alternate colour
     */
    private static final Color FOREGROUND_FOR_ALERT_ALT = Color.RED;

    /**
     * instruction normal color
     */
    private static final Color FOREGROUND_FOR_INSTRUCTIONS = new Color( 0x339911 );

    /**
     * foreground colour for title
     */
    private static final Color FOREGROUND_FOR_LABEL = Color.BLUE;

    /**
     * for titles
     */
    private static final Color FOREGROUND_FOR_TITLE = new Color( 0xdc143c );

    /**
     * URL colour
     */
    private static final Color FOREGROUND_FOR_URL = new Color( 0x442222 );

    /**
     * instruction colour when submitting
     */
    private static final Color FOREGROUND_FOR_WORKING = new Color( 0xccffcc/* light cyan */ );

    /**
     * instructions font
     */
    private static final Font FONT_FOR_INSTRUCTIONS = FontFactory.build( "Dialog", Font.PLAIN, 12 );

    /**
     * title font
     */
    private static final Font FONT_FOR_TITLE = FontFactory.build( "Dialog", Font.BOLD, 15 );

    /**
     * for title second line
     */
    private static final Font FONT_FOR_TITLE2 = FontFactory.build( "Dialog", Font.PLAIN, 14 );

    /**
     * URL font
     */
    private static final Font FONT_FOR_URLS = FontFactory.build( "Dialog", Font.PLAIN, 14 );

    /**
     * true if running as Applet, false if as application
     */
    private final boolean inApplet;

    /**
     * contentPane of the JApplet
     */
    private Container contentPane;

    /**
     * button to submit URL to various sites
     */
    private JButton submitButton;

    /**
     * aux instructions on how to use program
     */
    private JLabel instructions2;

    /**
     * label for pad name
     */
    private JLabel padFileLabel;

    /**
     * title for app
     */
    private JLabel title;

    /**
     * title, second line
     */
    private JLabel title2;

    /**
     * label for website URL
     */
    private JLabel websiteURLLabel;

    /**
     * control scrolling of the response field
     */
    private JScrollPane scroller;

    /**
     * text response from the website, with HTML stripped out
     */
    private JTextArea responsePage;

    /**
     * instructions on how to use program
     */
    private JTextField instructions;

    /**
     * name of the pad to submit e.g. entities.xml
     */
    private JTextField padFile;

    /**
     * instructions on how to use program
     */
    private JTextField response;

    /**
     * URL of the website directory http://mindprod.com/pad
     */
    private JTextField websiteURL;

    /**
     * where in registry we persist our history. key = value padname.xml = timestamp long for each submission. website =
     * http:\\mindprod.com\pad
     */
    private Preferences userPrefs;

    /**
     * the complete URL of the pad e.g. http://mindprod.com/pad/entitities.xml
     */
    private String fullPADURLString;

    /**
     * directory to dump the log, null if suppress log.*
     */
    private String logDir;

    /**
     * true if using alternate alert colour
     */
    private boolean usingAlt;

    /**
     * default constructor for Applet use.
     */
    public Submitter()
        {
        inApplet = true;
        }

    /**
     * Alternate constructor for standalone use.
     *
     * @param logDir directory to dump the log, null if suppress log.
     */
    private Submitter( String logDir )
        {
        inApplet = false;
        if ( logDir == null
             || logDir.length() == 0
             || logDir.equals( "null" )
             || logDir.equals( "default" )
             || logDir.equalsIgnoreCase( "noLog" ) )
            {
            logDir = null;
            }
        this.logDir = logDir;
        }

    /**
     * displays an alert message
     *
     * @param text string to display as alert message.
     */
    private void alert( String text )
        {
        assert text.trim().equals( text ) : "untrimmed alert text";
        // In case message is same as already there, we toggle the colour
        // to make it clear there is a "new" message.
        if ( text.equals( instructions.getText() ) )
            {
            usingAlt = !usingAlt;
            instructions.setForeground( usingAlt
                                        ? FOREGROUND_FOR_ALERT_ALT
                                        : FOREGROUND_FOR_ALERT );
            // no need to setText
            }
        else
            {
            instructions.setText( text );
            instructions.setForeground( FOREGROUND_FOR_ALERT );
            usingAlt = false;
            }
        }

    /**
     * build all the Swing components.
     */
    private void buildComponents()
        {
        contentPane.setBackground( BACKGROUND_FOR_BODY );
        title = new JLabel( TITLE_STRING + " " + VERSION_STRING );
        title.setFont( FONT_FOR_TITLE );
        title.setForeground( FOREGROUND_FOR_TITLE );
        title2 = new JLabel(
                "released:" +
                RELEASE_DATE +
                " 2026 Community Edition"
        );
        title2.setFont( FONT_FOR_TITLE2 );
        title2.setForeground( FOREGROUND_FOR_TITLE );
        websiteURLLabel = new JLabel( "Web Dir URL:", JLabel.RIGHT );
        websiteURLLabel.setForeground( FOREGROUND_FOR_LABEL );
        final String defaultWebsite;
        if ( userPrefs != null )
            {
            defaultWebsite = userPrefs.get( "website", SAMPLE_WEBSITE_URL );
            }
        else
            {
            defaultWebsite = SAMPLE_WEBSITE_URL;
            }
        websiteURL = new JTextField( defaultWebsite, 50 );
        websiteURL.setFont( FONT_FOR_URLS );
        websiteURL.setForeground( FOREGROUND_FOR_URL );
        websiteURL.setMargin( new Insets( 3, 2, 3, 2 ) );
        websiteURL.setToolTipText(
                "URL of directory or your website where you upload PAD xml files e.g. "
                + SAMPLE_WEBSITE_URL
        );
        padFileLabel = new JLabel( "PAD xml file:", JLabel.RIGHT );
        padFileLabel.setForeground( FOREGROUND_FOR_LABEL );
        padFile = new JTextField( SAMPLE_PAD_URL, 50 );
        padFile.setFont( FONT_FOR_URLS );
        padFile.setForeground( FOREGROUND_FOR_URL );
        padFile.setMargin( new Insets( 3, 2, 3, 2 ) );
        padFile.setToolTipText( "name of your uploaded PAD xml file e.g. "
                                + SAMPLE_PAD_URL );
        submitButton = new JEButton( "Submit" );
        submitButton.setToolTipText( "Submit this PAD xml to "
                                     + HOW_MANY_WEBSITES
                                     + " sites" );
        instructions = new JTextField( "To register your pad xml at "
                                       + HOW_MANY_WEBSITES
                                       + " distribution websites, enter the URL of your uploaded pad and click submit.",
                120
        );
        instructions.setFont( FONT_FOR_INSTRUCTIONS );
        instructions.setForeground( FOREGROUND_FOR_INSTRUCTIONS );
        instructions.setBackground( BACKGROUND_FOR_INSTRUCTIONS );
        instructions.setEditable( false );
        instructions.setMargin( new Insets( 2, 2, 2, 2 ) );
        instructions2 = new JLabel(
                "Don\u2019t scroll away from or minimise this Applet when it is actively submitting." );
        instructions2.setFont( FONT_FOR_INSTRUCTIONS );
        instructions2.setForeground( FOREGROUND_FOR_INSTRUCTIONS );
        instructions2.setBackground( BACKGROUND_FOR_INSTRUCTIONS );
        responsePage = new JTextArea();
        responsePage.setLineWrap( true );
        responsePage.setWrapStyleWord( true );
        // htmlDocument = new HTMLDocument();
        //  htmlDocument.setBase();  done later
        //  responsePage.setDocument( htmlDocument );
        // responsePage.setContentType( "text/html" );
        responsePage.setForeground( Color.BLACK ); // does not seem to work to set default CSS foreground.
        responsePage.setBackground( Color.WHITE );
        responsePage.setFont( FONT_FOR_INSTRUCTIONS );
        responsePage.setMargin( new Insets( 2, 2, 2, 2 ) );
        // contain the responsePage in JScrollPane.
        scroller = new JScrollPane( responsePage,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED );
        // control the speed effect of the wheelmouse
        scroller.getVerticalScrollBar().setUnitIncrement( 16 );
        response = new JTextField( "", 120 );
        response.setFont( FONT_FOR_INSTRUCTIONS );
        response.setForeground( FOREGROUND_FOR_INSTRUCTIONS );
        response.setBackground( BACKGROUND_FOR_INSTRUCTIONS );
        response.setEditable( false );
        response.setMargin( new Insets( 2, 2, 2, 2 ) );
        }

    /**
     * build a menu with Look & Feel and About across the top
     */
    private void buildMenu()
        {
        // turn on anti-alias
        System.setProperty( "swing.aatext", "true" );
        final JMenuBar menubar = new JMenuBar();
        setJMenuBar( menubar );
        final JMenu lafMenu = Laf.buildLookAndFeelMenu();
        if ( lafMenu != null )
            {
            menubar.add( lafMenu );
            }
        final JMenu menuHelp = new JMenu( "Help" );
        menubar.add( menuHelp );
        final JMenuItem aboutItem = new JMenuItem( "About" );
        menuHelp.add( aboutItem );
        aboutItem.addActionListener( new ActionListener()
            {
            public void actionPerformed( ActionEvent e )
                {
                // open about frame
                new CMPAboutJBox( Misc.getParentFrame( Submitter.this ),
                        TITLE_STRING,
                        VERSION_STRING,
                        "Submits an ASP PAD XML program description to "
                        + HOW_MANY_WEBSITES
                        + "websites.",
                        "",
                        "freeware",
                        RELEASE_DATE,
                        FIRST_COPYRIGHT_YEAR,
                        "Roedy Green",
                        "SUBMITTER",
                        "1.7"
                );
                }
            } );
        }

    /**
     * hook up the listeners
     */
    private void hookListeners()
        {
        submitButton.addActionListener( new ActionListener()
            {
            public void actionPerformed( ActionEvent e )
                {
                submit();
                }
            } );
        }

    /**
     * is the pad we are considering submitting valid?
     *
     * @return true if pad is valid
     */
    private boolean isPadValid()
        {
        String websiteURLString = websiteURL.getText().trim();
        String padFileString = padFile.getText().trim();
        if ( websiteURLString.length() == 0 )
            {
            alert( "You must fill in the website URL before hitting submit." );
            return false;
            }
        if ( padFileString.length() == 0 )
            {
            alert( "You must fill in the PAD URL before hitting submit." );
            return false;
            }
        if ( !websiteURLString.startsWith( "http://" ) && !websiteURLString.startsWith( "https://" ) )
            {
            alert( "The website URL ["
                   + websiteURLString
                   + "] must begin with http:// or https://" );
            return false;
            }
        if ( !padFileString.endsWith( ".xml" ) )
            {
            alert( "The PAD URL [" + padFileString + "] must end with .xml" );
            return false;
            }
        if ( websiteURLString.indexOf( '\\' ) >= 0 )
            {
            alert( "The website URL ["
                   + websiteURLString
                   + "] must not contain any \\ characters; use / instead." );
            return false;
            }
        if ( padFileString.indexOf( '\\' ) >= 0
             || padFileString.indexOf( '/' ) >= 0 )
            {
            alert( "The PAD URL ["
                   + padFileString
                   + "] must not contain any \\ or / characters." );
            return false;
            }
        if ( websiteURLString.equalsIgnoreCase( SAMPLE_WEBSITE_URL ) )
            {
            alert( "You must enter the URL of YOUR website before hitting submit." );
            return false;
            }
        if ( padFileString.equalsIgnoreCase( SAMPLE_PAD_URL ) )
            {
            alert( "You must enter the URL of YOUR pad on YOUR website before hitting submit." );
            return false;
            }
        final URL url;
        try
            {
            url = new URL( websiteURLString + '/' + padFileString );
            }
        catch ( MalformedURLException e )
            {
            alert( "Your URL ["
                   + websiteURLString
                   + '/'
                   + padFileString
                   + "] is malformed." );
            return false;
            }
        // we use Get instead of Probe because we want to test the length of the result, not just existence.
        final Get get = new Get();
        // no parms needed
        final String padText = get.send( url, Get.UTF8 );
        final int padResponseCode = get.getResponseCode();
        // later could check fields in the pad document
        if ( !get.isGood() || padText == null || padText.length() == 0 )
            {
            alert( "The PAD must already be uploaded to your website. responsecode:" + padResponseCode );
            return false;
            }
        if ( padText.length() < 5000 )
            {
            alert( "The uploaded PAD xml file should be 5000+ character long. It is only "
                   + padText.length()
                   + "." );
            return false;
            }
        // Later could check fields in the pad document
        // or extract field to use is submitting to trickier sites.
        final String lowerPadURLString = padFileString.toLowerCase();
        final long lastSubmitted;
        if ( userPrefs != null )
            {
            lastSubmitted = userPrefs.getLong( lowerPadURLString, 0 );  // key is pad name, value is timestamp.
            }
        else
            {
            lastSubmitted = 0;
            }
        final long now = System.currentTimeMillis();
        if ( lastSubmitted > now - ( 1000L * 60 * 60 * 24 * 7 ) )
            {
            // done before
            alert( padFileString + " already submitted within the last week." );
            if ( !PERMIT_RESUBMIT )
                {
                return false;
                }
            }
        // persist fact we are submitting this pad to all sites, just two fields.
        try
            {
            if ( userPrefs != null )
                {
                userPrefs.putLong( lowerPadURLString, now ); // key is pad name, value is usual Java timestamp long
                userPrefs.put( "website", websiteURLString );  // don't persist this until got a good one.
                // will persist the pad name later.
                userPrefs.flush();
                }
            }
        catch ( BackingStoreException e )
            {
            err.println( "Cannot save Preferences." );
            }
        // passed all tests, let it go
        return true;
        }

    /**
     * layout fields using GridBagLayout
     */
    private void layoutComponents()
        {
        // ---0-------------1------- -----2---
        // --title1---  -- title2 ------------ 0
        // ~webdir ---weburl------------------ 1
        //  ~pad     padurl   ---------submit  2
        // -----------instructions-----------  3
        // ----------scroller----------------  4
        // ----------response----------------  5
        // ----------instructions2-----------  6
        contentPane.setLayout( new GridBagLayout() );
        // x y w h wtx wty anchor fill T L B R padx pady
        contentPane.add( title,
                new GridBagConstraints( 0,
                        0,
                        1,
                        1,
                        0.0,
                        0.0,
                        GridBagConstraints.WEST,
                        GridBagConstraints.NONE,
                        new Insets( 10, 10, 5, 5 ),
                        0,
                        0 )
        );
        // x y w h wtx wty anchor fill T L B R padx pady
        contentPane.add( title2,
                new GridBagConstraints( 1,
                        0,
                        1,
                        1,
                        0.0,
                        0.0,
                        GridBagConstraints.CENTER,
                        GridBagConstraints.NONE,
                        new Insets( 10, 5, 5, 5 ),
                        0,
                        0 )
        );
        // x y w h wtx wty anchor fill T L B R padx pady
        contentPane.add( websiteURLLabel,
                new GridBagConstraints( 0,
                        1,
                        1,
                        1,
                        1.0,
                        0.0,
                        GridBagConstraints.EAST,
                        GridBagConstraints.NONE,
                        new Insets( 5, 10, 5, 5 ),
                        0,
                        0 )
        );
        // x y w h wtx wty anchor fill T L B R padx pady
        contentPane.add( websiteURL,
                new GridBagConstraints( 1,
                        1,
                        1,
                        1,
                        95.0,
                        0.0,
                        GridBagConstraints.EAST,
                        GridBagConstraints.BOTH,
                        new Insets( 5, 5, 5, 5 ),
                        0,
                        0 )
        );
        // x y w h wtx wty anchor fill T L B R padx pady
        contentPane.add( padFileLabel,
                new GridBagConstraints( 0,
                        2,
                        1,
                        1,
                        1.0,
                        0.0,
                        GridBagConstraints.EAST,
                        GridBagConstraints.NONE,
                        new Insets( 5, 10, 5, 5 ),
                        0,
                        0 )
        );
        // x y w h wtx wty anchor fill T L B R padx pady
        contentPane.add( padFile,
                new GridBagConstraints( 1,
                        2,
                        1,
                        1,
                        95.0,
                        0.0,
                        GridBagConstraints.EAST,
                        GridBagConstraints.BOTH,
                        new Insets( 5, 5, 5, 5 ),
                        0,
                        0 )
        );
        // x y w h wtx wty anchor fill T L B R padx pady
        contentPane.add( submitButton,
                new GridBagConstraints( 2,
                        2,
                        1,
                        1,
                        1.0,
                        0.0,
                        GridBagConstraints.EAST,
                        GridBagConstraints.NONE,
                        new Insets( 5, 5, 5, 10 ),
                        0,
                        0 )
        );
        // x y w h wtx wty anchor fill T L B R padx pady
        contentPane.add( instructions,
                new GridBagConstraints( 0,
                        3,
                        3,
                        1,
                        100.0,
                        0.0,
                        GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH,
                        new Insets( 5, 10, 5, 10 ),
                        0,
                        0 )
        );
        // x y w h wtx wty anchor fill T L B R padx pady
        contentPane.add( scroller
                /* contains responsePage */,
                new GridBagConstraints( 0,
                        4,
                        3,
                        1,
                        100.0,
                        100.0,
                        GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH,
                        new Insets( 5, 10, 5, 10 ),
                        0,
                        0 )
        );
        // x y w h wtx wty anchor fill T L B R padx pady
        contentPane.add( response,
                new GridBagConstraints( 0,
                        5,
                        3,
                        1,
                        100.0,
                        0.0,
                        GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH,
                        new Insets( 5, 10, 5, 10 ),
                        0,
                        0 )
        );
        // x y w h wtx wty anchor fill T L B R padx pady
        contentPane.add( instructions2,
                new GridBagConstraints( 0,
                        6,
                        3,
                        1,
                        100.0,
                        0.0,
                        GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH,
                        new Insets( 5, 10, 10, 10 ),
                        0,
                        0 )
        );
        }

    /**
     * Log the HTML response from the site
     *
     * @param site         which site.
     * @param siteResponse HTML from the site.
     */
    private void logSiteResponse( final Site site, final String siteResponse )
        {
        // for log.html files
        try
            {
            final BufferedWriter log = openLog( fullPADURLString, site.getName() );
            if ( log != null )
                {
                log.write( siteResponse );
                log.close();
                }
            }
        catch ( IOException e )
            {
            err.println( "logging not functioning" );
            }
        }

    /**
     * @param fullPADURLString URL of pad we are submitting
     * @param siteName         name of site we are submitting to
     *
     * @return BufferedWriter to put HTML output for this submission, or null to suppress log.
     * @throws java.io.FileNotFoundException if trouble creating output file
     */
    private BufferedWriter openLog( String fullPADURLString, String siteName ) throws FileNotFoundException
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
        if ( logDir == null )
            {
            return null;
            }
        else
            {
            final FileOutputStream fos = new FileOutputStream( new File( logDir,
                    padName + "_" + siteName + ".log.html" ), false /* append */ );
            final OutputStreamWriter osw = new OutputStreamWriter( fos );
            return new BufferedWriter( osw, 20000/* buffsize in chars */ );
            }
        }

    /**
     * submit URL to HOW_MANY_WEBSITES sites
     */
    private void submit()
        {
        submitButton.setEnabled( false );
        tidyWebsiteURL();
        tidyPadFilename();
        if ( isPadValid() )
            {
            fullPADURLString = websiteURL.getText().trim() + '/' + padFile.getText().trim();
            // submit to the HOW_MANY_WEBSITES sites
            instructions.setForeground( FOREGROUND_FOR_WORKING );
            instructions.setBackground( BACKGROUND_FOR_WORKING );
            response.setForeground( FOREGROUND_FOR_WORKING );
            response.setBackground( BACKGROUND_FOR_WORKING );
            new Thread( this ).start();
            }
        else
            {
            // if screwed up let user have another shot.
            submitButton.setEnabled( true );
            }
        }

    /**
     * tidy padFile  field
     */
    private void tidyPadFilename()
        {
        String padFileString = padFile.getText().trim();
        if ( padFileString.length() == 0 )
            {
            // We can't do anything with it.
            return;
            }
        padFileString = ST.trimLeading( padFileString, '/' );
        padFileString = ST.trimLeading( padFileString, '\\' );
        if ( !padFileString.endsWith( ".xml" ) )
            {
            padFileString += ".xml";
            }
        padFile.setText( padFileString );
        }

    /**
     * tidy websiteURL field
     */
    private void tidyWebsiteURL()
        {
        String websiteURLString = websiteURL.getText().trim();
        if ( websiteURLString.length() == 0 )
            {
            // We can't do anything with it.
            return;
            }
        if ( !websiteURLString.startsWith( "http://" ) && !websiteURLString.startsWith( "https://" ) )
            {
            websiteURLString = "http://" + websiteURLString;
            }
        if ( websiteURLString.endsWith( "/" ) )
            {
            websiteURLString = ST.trimTrailing( websiteURLString, '/' );
            }
        websiteURL.setText( websiteURLString );
        }

    /**
     * Allow this Applet to run as as application as well.
     *
     * @param args optional parm, directory to put logs.
     */
    public static void main( String args[] )
        {
        final String logDir = ( args.length >= 1 ) ? args[ 0 ] : null;
        HybridJ.fireup( new Submitter( logDir ),
                TITLE_STRING + " " + VERSION_STRING,
                APPLET_WIDTH,
                APPLET_HEIGHT );
        } // end main

    /**
     * Called by the browser or Applet viewer to inform
     * this Applet that it is being reclaimed and that it should destroy
     * any resources that it has allocated.
     */
    @Override
    public void destroy()
        {
        contentPane = null;
        fullPADURLString = null;
        // htmlDocument = null;
        instructions = null;
        instructions2 = null;
        logDir = null;
        padFile = null;
        padFileLabel = null;
        response = null;
        responsePage = null;
        scroller = null;
        submitButton = null;
        title2 = null;
        title = null;
        userPrefs = null;
        websiteURL = null;
        websiteURLLabel = null;
        }

    /**
     * Called by the browser or Applet viewer to inform
     * this Applet that it has been loaded into the system.
     */
    @Override
    public void init()
        {
        if ( inApplet )
            {
            //  use param only when run in a browser.
            logDir = this.getParameter( "logDir" );
            if ( logDir == null
                 || logDir.length() == 0
                 || logDir.equals( "null" )
                 || logDir.equals( "default" )
                 || logDir.equalsIgnoreCase( "noLog" ) )
                {
                logDir = null;
                }
            }
        if ( !VersionCheck.isJavaVersionOK( 1, 7, 0, contentPane ) )
            {
            // abort
            stop();
            destroy();
            }
        //  Common17.setLaf();
        contentPane = getContentPane();
        userPrefs = Preferences.userNodeForPackage( Submitter.class );
        usingAlt = false;
        buildMenu(); // also initial L&F
        buildComponents();
        layoutComponents();
        hookListeners();
        this.validate();
        this.setVisible( true );
        }

    /**
     * separate thread to handle submit loop
     */
    public void run()
        {
        if ( logDir == null )
            {
            out.println( "logging is turned off" );
            }
        else
            {
            out.println( "log files will appear in " + logDir );
            }
        out.println( "" );
        out.println( "-------------------------------" );
        out.println( "" );
        out.println( ">>>> SUBMITTING " + fullPADURLString );
        out.println( "" );
        responsePage.setText( "" );
        for ( Site site : SITES )
            {
            assert instructions != null : "instructions component not yet built.";
            instructions.setText( "Submitting to " + site.getName() + "." );
            String siteResponse = site.submit( fullPADURLString );
            if ( siteResponse == null )
                {
                siteResponse = "no response";
                }
            final int siteResponseCode = Site.getResponseCode();
            final String siteResponseMessage = Site.getResponseMessage();
            // render document relative to the website where the response came from.
            // htmlDocument.setBase( site.getBaseURL() );
            response.setText( "Response from: " + site.getName() + " >>>" + siteResponseCode + "<<< " +
                              siteResponseMessage );
            // for rolling log.
            out.println( "Response from: " + site.getName() + " >>>" + siteResponseCode + "<<< " +
                         siteResponseMessage );
            logSiteResponse( site, siteResponse );
            // display response from site with HTML stripped out. Do last so logs available on crash.
            responsePage.setText( ST.condense( DeEntifyStrings.flattenHTML( siteResponse,
                    ' ' ) ) );  // strips comments, tags, javascript
            try
                {
                // Sleep to give time to admire responsePage,
                // Does not include time for initial response.
                Thread.sleep( MILLIS_TO_ADMIRE );
                }
            catch ( InterruptedException e )
                {
                // nothing
                }
            } // end for
        SwingUtilities.invokeLater( new Runnable()
            {
            public void run()
                {
                instructions.setForeground( FOREGROUND_FOR_INSTRUCTIONS );
                instructions.setBackground( BACKGROUND_FOR_INSTRUCTIONS );
                instructions.setText( "D o n e !  Enter the URL of another pad xml file and click submit." );
                response.setText( "D o n e !  " + response.getText() );
                response.setForeground( FOREGROUND_FOR_INSTRUCTIONS );
                response.setBackground( BACKGROUND_FOR_INSTRUCTIONS );
                submitButton.setEnabled( true );
                }
            } );
        }
    }
