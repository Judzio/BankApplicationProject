W projekcie została zastosowana baza danych utworzona na lokalnym hoście za pomocą programu XAMPP,
aby poprawnie uruchomić bazę danych:

1. Pobierz program XAMPP z oficjalnej strony(https://www.apachefriends.org/pl/download.html) 

2. Wrzuć plik z utworzoną wcześniej bazą danych (folder "accounts") do ścieżki z bazami XAMPPA
orientacyjna (moja) ścieżka: C:\xampp\mysql\data\(tutaj folder accounts)

3. Dodanie connectora do projektu(jest w folderze), jeżeli go nie będzie (nie mam pewności) w IntelliJ jest to:
File -> Project Structure -> Libraries -> pierwszy plusik -> plik connectora w formie .jar -> apply

4. Po instalacji włączamy na XAMPP moduły "Apache" i "MySQL", poprzez guzik "Admin" (drugi od góry albo ewentualnie w przeglądarce wpisać "http://localhost/phpmyadmin/") przeniesie nas do zarządzania bazą danych (nasza to accounts, jeżeli została dodana)

5. DANE DO LOGOWANIA:
UŻYTKOWNIK:   login: judzio        hasło: Judzio123
ADMIN: 	      login: adminMati     hasło: haslo