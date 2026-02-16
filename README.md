# Configuration

Many properties are configurable in `application.yaml`.
- Blacklisted Country Codes: `client.country.blacklist`
- Blacklisted ISPs: `client.isp.blacklist`
- Option to toggle CSV file validation: `csv.validation.enabled`. If enabled, validation will fail-fast and return `BAD_REQUEST (400)`
- CSV files are *optionally* saved to `files.upload.directory`, depending on the value of `files.upload.save-file` (`true` or `false`)
- JSON files are saved to `files.download.directory`
- The fields written to the JSON file are configured under `outcome-file.json.fields`  

## Test Coverage

> 85% line coverage. Includes unit tests and end-to-end integration tests.

## CSV Generator

`com.concept.tools.EntryFileGenerator` is a tool for generating a dummy CSV file for testing purposes, e.g. with Postman.

## Database

Configured to use H2 in memory database for demonstration.

## Thoughts for future changes

- The IP address obtained in the controller may not be that of the end user, if a reverse proxy is used. Would need to inspect request headers, e.g. X-Forwarded-For
- An alternative to returning the JSON file as a response to the POST request would be to return the Request ID. Then provide an endpoint allowing clients to poll (GET) the file using the Request ID.
  Such an approach could be useful if file processing were to take a long time.