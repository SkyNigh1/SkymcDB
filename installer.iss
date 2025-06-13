[Setup]
AppName=SkymcDB
AppVersion=1.4
DefaultDirName={localappdata}\SkymcDB
DefaultGroupName=SkymcDB
OutputDir=.
OutputBaseFilename=SkymcDB_Installer_WithJava
Compression=lzma
SolidCompression=yes
DisableProgramGroupPage=yes
PrivilegesRequired=lowest

[Files]
Source: "SkymcDB.exe"; DestDir: "{app}"; Flags: ignoreversion
Source: "assets\*"; DestDir: "{app}\assets"; Flags: ignoreversion recursesubdirs
Source: "textures\*"; DestDir: "{app}\textures"; Flags: ignoreversion recursesubdirs
Source: "libs\*"; DestDir: "{app}\libs"; Flags: ignoreversion recursesubdirs
Source: "jdk-21.0.6.7-hotspot\*"; DestDir: "{app}\jdk"; Flags: ignoreversion recursesubdirs

[Icons]
Name: "{group}\SkymcDB"; Filename: "{app}\jdk\bin\java.exe"; IconFilename: "{app}\assets\logo.ico"; Parameters: "-jar {app}\SkymcDB.jar"; WorkingDir: "{app}"
Name: "{userdesktop}\SkymcDB"; Filename: "{app}\jdk\bin\java.exe"; IconFilename: "{app}\assets\logo.ico"; Parameters: "-jar {app}\SkymcDB.jar"; WorkingDir: "{app}"
