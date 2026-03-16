# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Fixed
- List and unknown type handling in pretty printer

## [0.4.0] - 2026-03-10
- Just for technical reasons, the version was bumped to 0.4.0 instead of 0.3.2, as the latter was already used for a previous release.

## [0.3.0] - 2026-03-10

### Changed
- Merge CompactPrinter into PrettyPrinter, as the compact printing functionality is now available via `PrettyPrinter.printCompact` method.

## [0.2.2] - 2026-03-02

### Fixed
- Handling of surrogate pair escape sequences (emoji via JSON `\uHHHH\uHHHH`) in `JSONParser` (test coverage added, see `JSONParserTest`).

## [0.2.1] - 2026-02-25

### Added
- PrettyPrinter to print without unnecessary newlines

## [0.2.0] - 2026-02-25

### Changed
- PrettyPrinter now returns the string and doesn't print it directly

## [0.1.0] - 2026-02-24

### Added
- Initial release of femtojson