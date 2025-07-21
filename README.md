# Symulator formuł analizy technicznej akcji giełdowych

## Wprowadzenie
Celem tego projektu było stworzenie aplikacji, która umożliwia testowanie różnych formuł analizy
technicznej na rynku akcji i ocenę ich skuteczności. Aplikacja ma charakter zarówno edukacyjny jak i
praktyczny kierując się głównie do studentów i pasjonatów giełdy oraz inwestowania. Pozwala
użytkownikom na testowanie różnych formuł inwestycyjnych z użyciem danych historycznych pobieranych
z Yahoo Finance.

Aplikacja oferuje cztery główne formuły analizy technicznej:
1. **SimpleUpAndDown** – autorska metoda opierająca się na analizie zmian cen akcji z ostatnich dni,
co ma na celu podjęcie decyzji o kupnie lub sprzedaży.
2. **Random** – formuła losowa, w której decyzje podejmowane są z określonymi
prawdopodobieństwami: 80% brak akcji, 10% kupno, 10% sprzedaż (służy głównie do testowania
działania interfejsu).
3. **RSI (Relative Strength Index)** – klasyczna formuła analizy technicznej, uwzględniająca takie
parametry jak: okres analizy, próg kupna i próg sprzedaży.
4. **MACD (Moving Average Convergence Divergence)** – popularny wskaźnik oparty na średnich
kroczących, wykorzystywany do analizy trendów rynkowych.

Aplikacja umożliwia interaktywne przeglądanie danych giełdowych. Centralnym elementem jest wykres,
na którym użytkownicy mogą monitorować notowania akcji oraz oznaczenia decyzji o kupnie i sprzedaży
na żywo za pomocą zielonych i czerwonych markerów. Dodatkowo aplikacja umożliwia zapisanie i
ponowne wykorzystanie formuły z określonymi parametrami.

W projekcie zastosowano dwa wzorce projektowe: **Strategia** i **Obserwator**. Wzorzec Strategia pozwala na
łatwą integrację różnych formuł analizy technicznej poprzez interfejs **TradingFormula**. Dzięki temu
możliwe jest dodawanie nowych strategii bez potrzeby zmiany głównego kodu aplikacji. Wzorzec
Obserwator zastosowano do obsługi pobierania danych z API Yahoo Finance, co pozwala na bieżąco
aktualizować wykresy i inne wizualizacje.
Projekt oferuje także funkcje umożliwiające podsumowanie rezultatu symulacji, takie jak: liczba
dokonanych transakcji, zwrot z inwestycji (ROI), zmiana wartości w analizowanym okresie oraz wzrost
kapitału przeznaczonego na symulację. Użytkownicy mogą testować różne kategorii aktywów, takie jak
akcje, krypto waluty, indeksy, fundusze ETF i ceny surowców naturalnych poprzez wpisanie odpowiedniego
symbolu w trakcie ściągania danych.
Projekt został stworzony z uwagi na osobiste zainteresowanie analizą techniczną.

<img width="807" height="589" alt="image" src="https://github.com/user-attachments/assets/2ead655c-e582-4fff-9cc5-d02b36bad70a" />

<img width="710" height="411" alt="image" src="https://github.com/user-attachments/assets/1073bac3-f9d9-4726-a861-b3cc7e3fd693" />

