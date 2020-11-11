
# CloudBerry

> Praca inżynierska AGH

[![Build Status](https://travis-ci.com/olliekrk/cloud-berry.svg?token=Ud4LPsJ6sjn1qVy7MUNS&branch=master)](https://travis-ci.com/olliekrk/cloud-berry)
[![codecov](https://codecov.io/gh/olliekrk/cloud-berry/branch/master/graph/badge.svg?token=ZCK0168E2G)](https://codecov.io/gh/olliekrk/cloud-berry)
[![HitCount](http://hits.dwyl.com/olliekrk/cloud-berry.svg)](http://hits.dwyl.com/olliekrk/cloud-berry)
[![License](http://img.shields.io/:license-mit-blue.svg?style=flat-square)](http://badges.mit-license.org)

---

## Table of Contents

- [Installation](#installation)
- [Requirements](#requirements)
- [Features](#features)
- [Team](#team)
- [License](#license)

---

## Installation

- `./gradlew build`
- `docker-compose -f docker/app.yml up`

### Requirements

- JRE 15
- Docker and docker-compose

### Konfiguracja aplikacji w trybie deweloperskim

Celem uruchomienia projektu w trybie deweloperskim należy przygotować środowisko uruchomieniowe, według wytycznych opisanych w sekcji wymagań, a następnie postępować krok po kroku zgodnie ze schematem z punktu drugiego.

#### Wymagania wstępne

Przed pełnym uruchomieniem systemu, należy zainstalować poniższe oprogramowanie:
* Java Development Kit 15
* Docker i docker-compose
* Node.js, w wersji co najmniej 15.1.0
* Node Package Manager, w wersji co najmniej 7.0.8
* Angular CLI, w wersji co najmniej 10.2.0
* Python, w wersji co najmniej 3.8, na potrzeby biblioteki `cloudberry-py`

Należy również upewnić się, że następujące porty sieciowe są lokalnie dostępne:
* 9000 TCP - dla aplikacji serwerowej cloudberry-cb
* 4200 TCP - dla aplikacji internetowej cloudberry-ng
* 9999 TCP - dla bazy danych InfluxDB
* 27017 TCP - dla bazy danych MongoDB
* 2181 TCP - dla usługi Zookeeper
* 9092 TCP - dla usługi Kafka
* 8125 UDP (opcjonalnie) - dla usługi Telegraf
* 8888 TCP (opcjonalnie) - dla usługi Jupyter Notebook

#### Uruchomienie systemu
Aby uruchomić system z poziomu głównego katalogu projektu należy wykonać poniższe akcje:

* Uruchomienie zewnętrznych serwisów, z których korzysta aplikacja cloud-berry należy wykonać przy użyciu polecenia
 docker-compose -f <nazwa pliku> up -d . Polecenie należy wywołać wielokrotnie, z poziomu podkatalogu `docker`, w
  miejscu argumentu `<nazwa pliku>` podając kolejno: kafka.yml, influxdb.yml, mongodb.yml oraz opcjonalnie monitoring.yml.
* Należy następnie upewnić się, czy Docker uruchomił wszystkie kontenery.
* Przy pierwszym uruchomieniu, w kontenerze `influxdb-cb` należy uruchomić polecenie ze skryptu `influx-setup.sh
`, znajdujcego się w folderze `docker/scripts`, które odpowiednio zainicjuje bazę danych - utworzy domyślne konto oraz
 tokeny dostępu.
    Można to zrobić przy pomocy `docker exec -it influxdb-cb -- /bin/bash` i następnie uruchamiając w kontenerze komendę z powyższego pliku.
* Celem weryfikacji, można sprawdzić czy interfejs bazy InfluxDB jest dostępny pod adresem `http://localhost:9999`. W domyślnej konfiguracji nazwa użytkownika to `root` a hasło `ziemniak`.
    Logowanie powinno zakończyć się z powodzeniem.
* Można następnie przystąpić do uruchomienia aplikacji serwerowej - `cloudberry-cb`. Należy uruchomić program z pliku `CloudberryApplication.java` z opcją `spring.profiles.active=dev`.
* Aplikacja serwerowa powinna już w pełni działać. Można w tym momencie korzystać z REST API lub biblioteki `cloudberry-py` do wgrywania i analizowania danych. Dokumentacja REST API dostępna jest pod adresem `http://localhost:9000/swagger-ui`.
* Aplikację internetową - `cloudberry-ng` należy uruchomić z poziomu folderu `cloudberry-ng`. Znajdując się w tym katalogu należy najpierw wykonać polecenie `npm install` a następnie uruchomić aplikację poleceniem `ng start`. Interfejs webowy dostępny jest pod adresem `http://localhost:4200`.

### Konfiguracja aplikacji w trybie produkcyjnym

#### Wymagania wstępne:
Przed produkcyjnym uruchomieniem systemu, należy zainstalować poniższe oprogramowanie:
* Docker i docker-compose
* Python, w wersji co najmniej 3.8, na potrzeby biblioteki `cloudberry-py`

Wymagana jest także dostępność identycznych portów jak w przypadku trybu deweloperskiego,
z tą różnicą, że aplikacja internetowa działa na porcie 90/TCP, a nie 4200/TCP.

#### Uruchomienie systemu:
W przypadku instalacji produkcyjnej, uruchomienie systemu wymaga zaledwie kilku kroków.
* Korzystamy z jednego pliku konfiguracyjnego `app-prod.yml`, i uruchamiamy go poleceniem 
`docker-compose -f app-prod.yml up -d`.
* Następnie należy powtórzyć krok z inicjalizacją bazy influx, wg zawartości skryptu z `scripts/influx_setup.sh
` analogicznie jak opisano w konfiguracji deweloperskiej.

Powyższe kroki powinny być wystarczające - poleceniem `docker ps -a` możemy zweryfikować czy wszystkie kontenery dzia
łają.

Po uruchomieniu, w domyślnej konfiguracji z pliku `app-prod.yml`, aplikacje będą dostępne pod adresami:
* http://localhost:90/ - aplikacja internetowa `cloudberry-ng` (frontend)
* http://localhost:9000/ - aplikacja serwerowa `cloudberry-cb` (backend)

Dodatkowo
* http://localhost:9999/ - interfejs graficzny bazy danych InfluxDB.
* http://localhost:9000/swagger-ui - dokumentacja REST API aplikacji serwerowej

---

## Features

- todo: Going into more detail on code and technologies used

### Metadata python api
#### experiment - cb_meta_experiment
 - `find_all()`
 - `find_by_id(experiment_id)`
 - `find_by_computation(computation)`
 - `find_by_configuration(configuration)`
 - `find_by_name(name)`
 - `find_or_create(name, parameters: dict = None)`
 - `update(experiment: Experiment, name: str = None, parameters: dict = None, override_params: bool = None)`
 - `delete(experiment)`

#### experiment configuration - cb_meta_experiment_configuration
- `find_all()`
- `find_by_id(configuration_id)`
- `find_by_computation(computation)`
- `find_by_experiment(experiment)`
- `find_by_configuration_name(configuration_name)`
- `find_by_experiment_name(name)`
- `find_or_create (experiment: Experiment, configuration_name: str = None, parameters: dict = None)`
- `update(configuration: ExperimentConfiguration, configuration_name: str = None, parameters: dict = None, override_params: bool = None)`
- `delete(configuration)`

#### experiment computation - cb_meta_experiment_computation
- `find_all()`
- `find_by_id(computation_id)`
- `find_by_configuration(configuration)`
- `find_by_experiment(experiment)`
- `create(configuration)`
- `delete(computation)`

---

## Team

| <a href="http://github.com/mimagiera" target="_blank">**Michał Magiera**</a> | <a href="http://github.com/olliekrk" target="_blank">**Olgierd Królik**</a> |
| :---: |:---:|
| [![mimagiera](https://avatars0.githubusercontent.com/u/43969709?s=200&v=4)](http://github.com/mimagiera)    | [![olliekrk](https://avatars3.githubusercontent.com/u/37264550?s=200&u=40b1359dfb778fe2ca75f57ed4e62acc203940a1&v=4)](http://github.com/olliekrk) |
| <a href="http://github.com/mimagiera" target="_blank">`github.com/mimagiera`</a> | <a href="http://github.com/olliekrk" target="_blank">`github.com/olliekrk`</a> |

---

## License

[![License](http://img.shields.io/:license-mit-blue.svg?style=flat-square)](http://badges.mit-license.org)

- **[MIT license](http://opensource.org/licenses/mit-license.php)**
