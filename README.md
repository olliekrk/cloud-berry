
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

- JRE 14
- Docker and docker-compose

---

## Features

- todo: Going into more detail on code and technologies used

### Metadata python api
#### experiment - cb_meta_experiment
 - `find_all()`
 - `find_by_id(experiment_id)`
 - `find_by_computation_id(computation_id)`
 - `find_by_configuration_id(configuration_id)`
 - `find_by_name(name)`
 - `find_or_create(name, parameters: dict = None)`
 - `update (experiment_id: str, name: str = None, parameters: dict = None, override_params: bool = None)`
 - `delete_by_id(experiment_id)`

#### experiment configuration - cb_meta_experiment_configuration
- `find_all()`
- `find_by_id(configuration_id)`
- `find_by_computation_id(computation_id)`
- `find_by_experiment_id(experiment_id)`
- `find_by_configuration_file_name(configuration_file_name)`
- `find_by_experiment_name(name)`
- `find_or_create (experiment_id: str, configuration_file_name: str = None, parameters: dict = None)`
- `update (configuration_id: str, configuration_file_name: str = None, parameters: dict = None, override_params: bool = None)`
- `delete_by_id (configuration_id)`

#### experiment computation - cb_meta_experiment_computation
- `find_all()`
- `find_by_id(computation_id)`
- `find_by_configuration_id(configuration_id)`
- `find_by_experiment_id(experiment_id)`
- `create(configuration_id)`
- `delete_by_id(computation_id)`

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
