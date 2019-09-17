// The file contents for the current environment will overwrite these during build.
// The build system defaults to the dev environment which uses `environment.ts`, but if you do
// `ng build --env=prod` then `environment.prod.ts` will be used instead.
// The list of which env maps to which file can be found in `.angular-cli.json`.yar

export const environment = {
  production: false,
  //serverUrl: 'https://api.popflyxp.com:8443/api/api' //Production
  //serverUrl: 'http://35.199.43.24:8080/api/api' //Development
  serverUrl: 'http://192.168.100.10:8080/api' //Development
};
