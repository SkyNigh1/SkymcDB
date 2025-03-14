[Setup]
AppName=SkymcDB
AppVersion=1.2
DefaultDirName={localappdata}\SkymcDB
DefaultGroupName=SkymcDB
OutputDir=.
OutputBaseFilename=SkymcDB_Installer
Compression=lzma
SolidCompression=yes
DisableProgramGroupPage=yes
PrivilegesRequired=lowest 

; Dossier où les fichiers seront copiés
[Files]
Source: "SkymcDB.exe"; DestDir: "{app}"; Flags: ignoreversion
Source: "SkymcDB.jar"; DestDir: "{app}"; Flags: ignoreversion
Source: "textures\*"; DestDir: "{app}\textures"; Flags: ignoreversion recursesubdirs
Source: "class\*"; DestDir: "{app}\class"; Flags: ignoreversion recursesubdirs
Source: "src\*"; DestDir: "{app}\src"; Flags: ignoreversion recursesubdirs
Source: "jdk-21.0.6.7-hotspot\*"; DestDir: "{app}\jdk"; Flags: ignoreversion recursesubdirs 
Source: "face.ico"; DestDir: "{app}"; Flags: ignoreversion

; Exécuter l'application avec le JDK local après installation
[Run]
Filename: "{app}\jdk\bin\java.exe"; Parameters: "-jar {app}\SkymcDB.jar"; StatusMsg: "Lancement de l'application..."; Flags: postinstall

[Icons]
; Crée un raccourci dans le menu Démarrer
Name: "{group}\SkymcDB"; Filename: "{app}\jdk\bin\java.exe";IconFilename: "{app}\face.ico"; Parameters: "-jar {app}\SkymcDB.jar"; WorkingDir: "{app}"

[Code]
var
  ResultCode: Integer;

procedure CurStepChanged(CurStep: TSetupStep);
begin
  if CurStep = ssPostInstall then
  begin
    ShellExec('', ExpandConstant('{app}\jdk\bin\java.exe'), '-jar "' + ExpandConstant('{app}\SkymcDB.jar') + '"', '', SW_SHOWNORMAL, ewNoWait, ResultCode);
  end;
end;
