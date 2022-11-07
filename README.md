# pstconv-autopsy-module

A NetBeans module that integrates [pstconv](https://github.com/cjmach/pstconv) tool in [Autopsy](https://www.sleuthkit.org/autopsy/)&reg;. It provides context menu options to convert proprietary Microsoft Outlook OST/PST files to EML or MBOX format.

# Introduction

Autopsy&reg; is a digital forensic application used by law enforcement, military, and corporate examiners to analyse the contents of a computer. It's built on top of NetBeans Platform, a rich client java platform that provides several APIs and a module system to help write large desktop applications, like NetBeans IDE for example. Autopsy also takes advantage of this module system to provide a set of key features such as file identification and indexing, extracting web browser bookmarks and navigation history and more.

One of the modules that is included in the default installation is the 'Email Parser' module. It can parse and extract e-mail messages in several mail box formats, such as Mozilla Thunderbird folders and PST archives and it's also possible to do some basic filtering and searching but it lacks features for advanced searching which are normally available on e-mail client applications such as Microsoft Outlook, Mozilla Thunderbird, Opera Mail, just to name a few.

The PSTConv Autopsy Module allows to convert a [Microsoft PST archive](https://learn.microsoft.com/en-us/openspecs/office_file_formats/ms-pst/) to MBOX or EML format so that it's possible to import the converted message to an e-mail client application for further analysis. 

# Installing

Download the latest version of PSTConv Autopsy Module from the [Releases](https://github.com/cjmach/pstconv-autopsy-module/releases) (the file with .nbm extension), then open Autopsy and click on the Tools -> Plugins menu item. Select the 'Downloaded' tab from the 'Plugins' window and browse to the downloaded module file by clicking on the 'Add Plugins...' button. Afterwards, hit the 'Install' button to install the module.

# Usage

In order to use this plugin effectively, the user must first run the 'File Identification' module so that Autopsy calculates the mime type of each file contained in the datasource. Right-clicking on a file with "application/vnd.ms-outlook-pst" or "application/vnd.ms-outlook" mime types will popup a context menu with two additional items added by the PSConv Autopsy Module.
