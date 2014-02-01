[Setup]
AppName=JBirthdays
AppVerName=JBirthdays 0.4.2
AppVersion=0.4.2
AppPublisher=Elmar Baumann <eb@elmar-baumann.de>
AppPublisherURL=http://elmar-baumann.de/JBirthdays/
AppSupportURL=mailto:eb@elmar-baumann.de
AppUpdatesURL=http://elmar-baumann.de/JBirthdays/download.html
AppComments=Informs about birthdays in present, future and past
DefaultDirName={pf}\JBirthdays
DefaultGroupName=JBirthdays
AllowNoIcons=yes
OutputDir={#SourcePath}\..\..\dist_files\upload
OutputBaseFilename=JBirthdays-setup
SetupIconFile={#SourcePath}\JBirthdays.ico
WizardImageFile={#SourcePath}\WizardImageFile.bmp
WizardSmallImageFile={#SourcePath}\WizardSmallImageFile.bmp
Compression=zip
UninstallDisplayIcon={app}\unins000.exe
ArchitecturesInstallIn64BitMode=x64 ia64

[Languages]
Name: "de"; \
  MessagesFile: "compiler:Languages\German.isl"; \
  InfoAfterFile: "JBirthdays-Readme_de.txt"
Name: "en"; \
  MessagesFile: "compiler:Default.isl"; \
  InfoAfterFile: "JBirthdays-Readme_en.txt"

[Tasks]
Name: "desktopicon"; \
  Description: "{cm:CreateDesktopIcon}"; \
  GroupDescription: "{cm:AdditionalIcons}"; \
  Flags: unchecked

[Files]
Source: "{#SourcePath}\..\..\Application\dist\JBirthdays.jar"; \
  DestDir: "{app}"; \
  AfterInstall: CreateBatchFile; \
  Flags: ignoreversion
Source: "{#SourcePath}\..\..\Application\dist\lib\*"; \
  DestDir: "{app}\lib"; \
  Flags: ignoreversion
Source: "{#SourcePath}\JBirthdays.ico"; \
  DestDir: "{app}"; \
  Flags: ignoreversion

[Icons]
Name: "{group}\JBirthdays"; \
  Filename: "{app}\JBirthdays.bat"; \
  IconFilename: "{app}\JBirthdays.ico"; \
  Flags: runminimized
Name: "{group}\Handbuch (PDF)"; \
  Filename: "{app}\Manual_de.pdf"
Name: "{group}\{cm:ProgramOnTheWeb,JBirthdays}"; \
  Filename: "http://elmar-baumann.de/JBirthdays/"; \
  IconFilename: "{app}\JPhotoTagger.ico"
Name: "{commondesktop}\JBirthdays"; \
  Filename: "{app}\JBirthdays.bat"; \
  Tasks: desktopicon; \
  IconFilename: "{app}\JBirthdays.ico"; \
  Flags: runminimized

[Run]
Filename: "{app}\JBirthdays.bat"; \
  Description: "{cm:LaunchProgram,JBirthdays}"; \
  Flags: 32bit nowait postinstall skipifsilent runminimized; \
  Check: not IsWin64
Filename: "{app}\JBirthdays.bat"; \
  Description: "{cm:LaunchProgram,JBirthdays}"; \
  Flags: 64bit nowait postinstall skipifsilent runminimized; \
  Check: IsWin64

[Messages]
de.WelcomeLabel2=[name/ver] wird installiert.
de.FinishedLabel=Fertig. Bitte denken Sie daran, dass Java 7 (1.7) oder höher installiert sein muss!

en.WelcomeLabel2=[name/ver] will be installed.
en.FinishedLabel=Fertig. Please ensure, that Java 7 (1.7) or higher is installed!

[CustomMessages]
de.JvmUserLanguageCaption=Sprache
de.JvmUserLanguageDescription=JBirthdays Sprache
de.JvmUserLanguageSubCaption=JBirthdays Benutzeroberflächen-Sprache
de.JvmUserLanguageOptionAuto=Automatisch
de.JvmUserLanguageOptionDe=Deutsch
de.JvmUserLanguageOptionEn=Englisch

en.JvmUserLanguageCaption=Language
en.JvmUserLanguageDescription=JBirthdays' Language
en.JvmUserLanguageSubCaption=JBirthdays' GUI Language
en.JvmUserLanguageOptionAuto=Automatically
en.JvmUserLanguageOptionDe=German
en.JvmUserLanguageOptionEn=English

[Code]
const
  JPT_REGISTRY_KEY = 'Software\JBirthdays';
  USER_LANGUAGE_INDEX_REG_VALUE_NAME = 'InstallerUserLanguageIndex';

var
  UserLanguage: String;
  UserLanguagePage: TInputOptionWizardPage;

function GetUserLanguageIndex(): Cardinal;
var
  Index: Cardinal;
begin
  if (RegQueryDWordValue(HKEY_CURRENT_USER, JPT_REGISTRY_KEY, USER_LANGUAGE_INDEX_REG_VALUE_NAME, Index))
  then Result := Index
  else Result := 0;
end;

procedure StoreUserLanguageIndex(const Index: Cardinal);
begin
  RegWriteDWordValue(HKEY_CURRENT_USER, JPT_REGISTRY_KEY, USER_LANGUAGE_INDEX_REG_VALUE_NAME, Index);
end;

procedure CreateUserLanguagePage();
begin
  UserLanguagePage := CreateInputOptionPage(wpWelcome,
            ExpandConstant('{cm:JvmUserLanguageCaption}'),
            ExpandConstant('{cm:JvmUserLanguageDescription}'),
            ExpandConstant('{cm:JvmUserLanguageSubCaption}'),
            true, { Exclusive }
            false { ListBox }
  );

  UserLanguagePage.Add(ExpandConstant('{cm:JvmUserLanguageOptionAuto}'));
  UserLanguagePage.Add(ExpandConstant('{cm:JvmUserLanguageOptionDe}'));
  UserLanguagePage.Add(ExpandConstant('{cm:JvmUserLanguageOptionEn}'));

  UserLanguagePage.Values[GetUserLanguageIndex()] := true;
end;

procedure SetUserLanguage();
begin
  case UserLanguagePage.SelectedValueIndex of
    1 : UserLanguage := ' -Duser.language=de';
    2 : UserLanguage := ' -Duser.language=en';
    else UserLanguage := '';
  end;

  StoreUserLanguageIndex(UserLanguagePage.SelectedValueIndex);
end;

procedure CreateBatchFile();
var
  BatchFileName: String;
  DirectoryName: String;
  CommandLine: String;
begin
  DirectoryName := ExpandConstant('{app}');
  BatchFileName := DirectoryName + '\JBirthdays.bat';
  CommandLine := 'start javaw -jar ' + UserLanguage + ' "' + DirectoryName + '\JBirthdays.jar"';
  SaveStringToFile(BatchFileName, CommandLine, False);
end;

procedure InitializeWizard();
begin
  CreateUserLanguagePage();
end;

function NextButtonClick(CurPageID: Integer): Boolean;
begin
  case CurPageID of
    UserLanguagePage.ID : SetUserLanguage();
  end;
  Result := True;
end;

