
#include <Array.au3>

Local $path = @ScriptDir & "\data.ini"
Local $directory = IniRead($path, "Propriedades", "Diretorio", "")
Local $folder = StringReplace($directory, "chromedriver.exe", "")
Local $sectionNames = IniReadSectionNames($path)
Local $ini[$sectionNames[0] - 1][3]

If $directory <> "" Then
   ; Copiar o driver caso nao exista no local desejado.
   If FileExists($directory) Then
	  ConsoleWrite('Arquivo existe: ' & $directory & @CRLF)
   Else
	  DirCreate($folder)
	  ConsoleWrite('Criar o diretorio: ' & $folder & @CRLF)
	  FileCopy(@ScriptDir & '\chromedriver.exe', $directory, 9)
	  ConsoleWrite('Arquivo copiado: ' & $directory & @CRLF)
   EndIf

   ; Executar o atualizador de paginas.
   If FileExists(@ScriptDir & '\PanelRefresher.jar') Then
	  ShellExecute('java', '-jar "PanelRefresher.jar', @ScriptDir, Default, @SW_MINIMIZE)
   EndIf
EndIf

; Obter as configuracoes.
For $index = 1 To $sectionNames[0]
   If $sectionNames[$index] <> "Propriedades" Then
	  $ini[$index - 2][0] = $sectionNames[$index]
	  $ini[$index - 2][2] = IniRead($path, $sectionNames[$index], "Foco", 0)
   EndIf
Next

; Obter as referencias.
While Not isCompleted($ini)
   For $index = 0 To UBound($ini) - 1
	  Local $indexx = getIndex($ini, $ini[$index][0])
	  If $indexx <> -1 Then
		 Local $handle = getHandle($ini[$indexx][0])
		 $ini[$indexx][1] = $handle
	  EndIf
   Next
   Sleep(1000)
WEnd

; Alternar as paginas.
While True
   For $index = 0 To UBound($ini) - 1
	  WinSetOnTop($ini[$index][1], "", 1)
	  Sleep($ini[$index][2] * 1000)
   Next
WEnd

#cs
Verificar se existe o nome.
Author: Gugatb
Date: 10/10/2018
Param: ini o arquivo ini
Param: name o nome
Return true se existe, caso contrario false
#ce
Func exists($ini, $name)
   Local $exists = False

   For $index = 0 To UBound($ini) - 1
	  If $ini[$index][0] == $name Then
		 $exists = True
	  EndIf
   Next
   Return $exists
EndFunc

#cs
Verificar se completou.
Author: Gugatb
Date: 10/10/2018
Param: ini o arquivo ini
Return true se completou, caso contrario false
#ce
Func isCompleted($ini)
   Local $isCompleted = True

   For $index = 0 To UBound($ini) - 1
	  If $ini[$index][1] == "" Then
		 $isCompleted = False
	  EndIf
   Next

   If UBound($ini) == 0 Then
	  $isCompleted = False
   EndIf
   Return $isCompleted
EndFunc

#cs
Obter a referencia
Author: Gugatb
Date: 10/10/2018
Param: name o nome
Return handle a referencia
#ce
Func getHandle($name)
   Local $handle = ""
   Local $list = WinList()

   For $index = 1 To $list[0][0]
	  If StringInStr($list[$index][0], $name) Then
		 $handle = $list[$index][1]
	  EndIf
   Next
   Return $handle
EndFunc

#cs
Obter o indice.
Author: Gugatb
Date: 10/10/2018
Param: ini o arquivo ini
Param: name o nome
Return index o indice
#ce
Func getIndex($ini, $name)
   Local $index = -1

   For $indexx = 0 To UBound($ini) - 1
	  ConsoleWrite($ini[$indexx][0] & " = " & $name & @CRLF)
	  If $ini[$indexx][0] == $name Then
		 $index = $indexx
	  EndIf
   Next
   Return $index
EndFunc
