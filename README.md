# StonksBot

StonksBot is a discord bot developed for a private server with features including fetching stock market data.

## Table of Contents

  * [Installation](#Installation)
    * [Prerequisites](#Prerequisites)
    * [Building](#Building)
    * [Configuration](#Configuration)
  * [Contributing](#Contributing)
  * [Commands](#Commands)

## Installation

### Prerequisites
You need to have Java 11 JDK installed on your system to run and compile the application.

### Building

```
git clone git@github.com:etsubu/StonksBot.git
cd StonksBot
./gradlew clean build
```

The application .jar file will be located in build/libs/

## Configuration

StonksBot requires you to provide discord oauth key in the configuration file. 
Place config.yaml file in the same directory with the .jar file and place the oauth key in there. 
Example: 

```
oauth: AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
```

## Contributing

Some of the bots features are kind a niche such as lunch list which is due to usage and thus requirements of a 
private server. However, new commands can be easily added by creating new files in Core/Commands/ folder and 
implementing Command interface. The bot finds available commands during runtime meaning there is no need to register 
the commands in a separate place. 

Make sure the code compiles, tests pass, and verification plugins pass, before opening a Pull Request.

## Commands

Stock names are resolved by using search functionality in yahoo finance and the result is cached. This 
means that you can either use the stock ticker for searching or the prefix of the full stock name.
* `!price [stock]`
    * This will query the intraday price data for the given stock
    * Example: `!price msft`
        * This will query price data for Microsoft
        * Example response: 
            ```
          MSFT
          Price: 213.02
          Change: -0.73%
          Open: 213.86
          High: 216.25
          Low: 212.85
          Volume: 36 152 200
          ```
* `!calendar [stock]`
    * This command will query calendar event such earnings date, dividend date and EPS forecasts
    Note that only fields that are available are displayed
    * Example: `!calendar msft`
        * This will query calendar events for Microsoft
        * Example response:
            ```
          Earnings date: 21-10-2020
          Forecast EPS avg: 1,54
          Forecast EPS high: 1,61
          Forecast EPS low: 1,49
          Forecast Revenue avg: 35 689 500 000
          Forecast Revenue high: 36 394 000 000
          Forecast Revenue low: 35 299 100 000
          Previous dividend date: 19-08-2020
          Next dividend date: 10-09-2020
          ```
* `!lunch`
    * This command will display the lunch list for a couple of restaurants at University of Jyväskylä. 
    As mentioned previously, some of the bot's features are really niche. Before 18PM EEST the command 
    returns the current days lunch list, and after 18PM it displays next days lunch list.
    * Example `!lunch`
        * This returns the lunch list
        * Example response: 
            ```
          Ravintola Piato
          2020-08-19
          KASVISLOUNAS
              Pinaattiohukaisia (* ,A)
              Puolukkahilloa (G ,L ,M ,Veg)
          LOUNAS
              Sinappi-porsaspataa (* ,A ,L)
          LOUNAS
              Katkarapuja ja kasviksia itämaisessa kastikkeessa (G ,L ,M)
          PAISTOPISTE
          JÄLKIRUOKA
              Passionrahkaa (A ,G ,L)
          LOUNAS
              Paahdettua kirjolohta A, G, L ()
          
          Ravintola Maija
          2020-08-19
          KASVISLOUNAS
              Kikherne-bataattipataa (* ,A ,G ,L ,M ,Veg ,VS)
              Höyrytettyä tummaa riisiä (* ,G ,L ,M ,Veg)
          LOUNAS
              Porsas-paprikakastiketta (* ,A ,G ,L)
              Keitettyjä perunoita (* ,G ,L ,M ,Veg)
          LOUNAS
              Broileri-fetapizzaa BBQ (A ,VL ,VS)
              Basilikaöljyä (* ,G ,L ,M ,Veg)
          KEITTOLOUNAS
              Aasialaista lohi-seitikeittoa (* ,A ,G ,L ,M)
          JÄLKIRUOKA
              Mansikkarahkaa (A ,G ,L)
          ```
        
