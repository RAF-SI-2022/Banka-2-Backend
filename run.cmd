@echo off

rem This script is used for manipulating all projects in this repository, on
rem a per-project and global level (all projects at once).

goto :main

rem Initializes the JDK, Git repo settings/hooks, etc. Sets up the development
rem environemnt.
:init
    setlocal
        rem Disable auto-modification of CR/LF endings
        echo Disabling git config core.autocrlf (this repo)...
        call git config core.autocrlf false
        echo Done

        rem Copy git hooks
        echo Copying git hooks...
        call xcopy "git\hooks" ".git\hooks" /E /C /H /R /K /Y
        echo Done

        echo Cleaning old !jdk! folder...
        call rmdir %jdk% /s /q >NUL 2>&1
        call del lib\sha_comp_0.txt >NUL 2>&1
        call del lib\sha_comp_1.txt >NUL 2>&1
        if not exist "lib" mkdir lib
        echo Done

        rem Download the package
        echo Downloading Amazon Corretto JDK and checksum...
        call curl -Lo ./lib/%targetJdk% %sourceJdk%

        rem Download the checksum
        call curl -Lo ./lib/%targetSha% %sourceSha%
        echo Done

        rem Check SHA256
        cd lib
        echo Verifying JDK checksum...
        set /p sha_dl=<%targetSha%

        rem Calculate hashes
        >sha_comp_0.txt (
            certutil -hashfile %targetJdk% SHA256
        )
        >sha_comp_1.txt (
            findstr /r /c:"^[a-z0-9]*$" sha_comp_0.txt
        )

        rem Set EOF after 64 chars and read into var
        call fsutil file seteof sha_comp_1.txt 64 1> NUL
        set /p sha_comp=<sha_comp_1.txt

        rem Compare checksums
        if "!sha_dl!" NEQ "!sha_comp!" (
            echo Bad SHA, do ./run init again:
            echo SHA downloaded: !sha_dl!
            echo SHA computed:   !sha_comp!
            exit 1
        )
        echo Done

        rem Delete checksum files
        call del sha_comp_0.txt >NUL 2>&1
        call del sha_comp_1.txt >NUL 2>&1

        rem Unpack
        echo Unpacking JDK...
        call tar -xf %targetJdk%

        rem Remove residue
        del %targetJdk%
        del %targetSha%
        cd ..

        rem Move files directly into jdk dir
        move /y lib\jdk* %jdk%
        echo Init complete
    endlocal
exit /B 0

rem Compiles the .build project.
:compileBuild
    setlocal
        %bin%\javac -cp .build\src ^
            -d .build\out ^
            .build\src\rs\edu\raf\si\bank2\Main.java
    endlocal
exit /B 0

rem Runs any commands other than init through the Java build script with JDK.
:run
    setlocal
        set JAVA_HOME=%projectHome%\%jdk%
        %bin%\java -cp .build\out rs.edu.raf.si.bank2.Main %*
    endlocal
exit /B 0

rem Main script logic.
:main
    setlocal enableextensions enabledelayedexpansion

        set projectHome=%cd%
        set targetJdk=amazon-corretto-17-x64-windows-jdk.zip
        set sourceJdk=https://corretto.aws/downloads/latest/%targetJdk%
        set targetSha=amazon-corretto-17-x64-windows-jdk.zip.checksum
        set sourceSha=https://corretto.aws/downloads/latest_sha256/%targetJdk%
        set jdk=lib\jdk
        set bin=%cd%\%jdk%\bin

        if "%~1" == "init" (
            call :init
        ) else (
            if "%~1" == "compileBuild" (
                call :compileBuild
                exit 0
            )

            if exist %jdk%\bin  (
                call :run %* ^
                "--shellCommand" "cmd" "--shellStartTokenCount" "1" ^
                 "--shellStartTokens" "/c" "--platform" "windows"
            ) else (
                call java --version >NUL
                if %errorlevel% == 0 (
                    call java -cp .build\out rs.edu.raf.si.bank2.Main %* ^
                        "--shellCommand" "cmd" "--shellStartTokenCount" "1" ^
                        "--shellStartTokens" "/c" "--platform" "windows"
                ) else (
                    echo Java not installed! Run init or install Java first
                    exit 1
                )
            )
        )
    endlocal
exit /B 0
