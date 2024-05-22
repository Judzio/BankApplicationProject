Projekt jest symulacją aplikacji bankowej na potrzeby projektu uczelnianego, której
głównym zamysłem miała być współbieżna obsługa klientów (lokalnych w tym przypadku).

W projekcie użyłem lokalnie postawionej bazy danych za pośrednictwem XAMPP, do której będzie
potrzebna krótka instalacja oraz konfiguracja - szczegóły w folderze "XAMPP" oraz notatce README_BAZA_DANYCH

---------------------------------------------------------------------------------------------------

Opis poszczególnych klas:

BankClientServer - nieskończona pętla while w nasłuchu do akceptowania kolejnych klientów na określonym porcie oraz tworzenia dla nich nowego wątku gdzie klient będzie obsługiwany (wielowątkowość)

BankClientHandler - utworzony wątek, obsługa klienta w głównej pętli gdzie klasa jest ustawiona na odsłuch lini od klienta, następnie wiadomość jest interpretowana i wysyłana jest odpowiedź

Client - aplikacja klienta, komunikacja z "BankClientHandler" w głownej pętli while. Możliwe jest uruchomienie wielu klientów i logowanie na wiele kont jednocześnie

Kolejnym zestawem klas jest BankAdminServer, BankAdminHandler, Client, które działają na takiej samej zasadzie
co poprzednie 3 ale mają dostosowaną funkcjonalność pod "moderowanie" użytkownikami.

---------------------------------------------------------------------------------------------------

Projekt jest podzielony na dwie części:

 Aplikacja użytkownika banku - aby uruchomić najpierw musimy włączyć i skonfigurować bazę danych (tak jak w notatce README-BAZA-DANYCH), uruchomić klase "BankClientServer" (nie pomylić z BankAdminServer, ponieważ działa na innym porcie), a następnie włączyć klasę "Client"

  1. Panel logowania i rejestracji 

a) logowanie - dokonujemy wyboru operacji poprzez wpisanie "login", wpisujemy nasz login, a następnie hasło. Przykładowi użytkownicy są juz stworzeni, poniżej będą sie znajdować najważniejsze dane. Po zalogowaniu dostajemy dostęp do następnego panelu, jest to panel z operacjami. Logowanie na TO SAMO konto dopuszcza tylko jednego użytkownika na raz.

b) rejestracja - dokonujemy wyboru poprzez wpisanie "register" w terminalu. Opcja ta ma na celu utworzyć użytkownika (w momencie tworzenia aplikacji klienckiej nie bylo wymogu od wykładowcy implementacji admina dlatego ta opcja zostala). Rejestracja ma wymogi typu PESEL tylko liczby, 11 cyfr.

c) quit - wyłączenie aplikacji


  2. Panel zalogowanego uzytkownik - operacje

a) balance - wpisujemy "balance" i otrzymujemy aktualny stan konta

b) add money - aby użyć wpisujemy w terminalu "add money", następnie bedziemy poproszeni o kwote. Operacja ta to symulacja wplaty pieniędzy we wplatomacie.

c) withdraw money - aby użyć wpisujemy "withdraw money", następnie będziemy poproszeni o kwote. Operacja to symulacja wybierania pieniędzy z bankomatu.

d) transfer money - wpisujemy "transfer money" , następnie zostaniemy poproszeni o numer konta(dane poniżej) oraz o kwote ktora chcemy przesłać na istniejące konto.

e) logout- wylogowuje z konta oraz wraca do panelu menu logowania
Wysłano

---------------------------------------------------------------------------------------------------
 
Aplikacja admina banku -aby uruchomić potrzebna jest baza danych, uruchomić klasę "BankAdminServer", a następnie klasę "Admin"


  1. Panel logowania I rejestracji 

a) login - dziala na tej samej zasadzie co u klienta, przykładowe dane poniżej 

b) quit - wychodzi z aplikacji


  2. Panel operacji admina

a) add client - komenda "add client" przechodzi przez proces dodawania klienta

b) client list - komenda "client list" wypisuje liste klientow oraz ich dane

c) modify client - komenda "modify client", następnie aplikacja poprosi o login użytkownika, którego chcemy modyfikować. Proces modyfikacji zmienia tylko, niektóre dane

d) delete client - komenda "delete client", następnie aplikacja prosi o login użytkownika, któego chcemy usunąć. 

e) logout - wylogowuje z konta admina I wraca do panelu logowania

---------------------------------------------------------------------------------------------------


DANE POTRZEBNE DO OBSŁUGI APLIKACJI (jeżeli został wklejony plik "accounts"):

Klienci: 

1.
login: judzio
hasło: Judzio123
name: test
surname: testsurname
transfer_number: 21438392421572876138295239

2.
login: olka
hasło: olkaOLKA123
name: ola
surname: polanska
transfer_number: 96761221308794734813539631

Admini:

1. 
login: adminMati
hasło: haslo

2.
login: adminPiotr
hasło: haslo


---------------------------------------------------------------------------------------------------

   
