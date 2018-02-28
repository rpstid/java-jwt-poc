# Generate JWT using nimbus-jose-jwt lib PoC 

## Build the JWT
Our application should generate the JWT that represents to the customer.
A JWT consists of three Base64 encoded parts separated by dots, which are:
- header
- payload
- signature

Therefore, a JWT typically looks like the following:
xxxxx.yyyyy.zzzzz

The header is a JSON like the following:
```
{
  "typ": "JWT",
  "alg": "RS256"
}
```
Where:
- typ must be set to JWT.
- alg is the algorithm used to sign the JWT.

The payload is a JSON like the following:
```
{
  "sub": "412d606f-4937-443b-b5e7-a8d0f63ef0bc",
  "aud": "https://auth.default-aws-nightly.e2etest.baikalplatform.com/",
  "scope": "SCOPE1 SCOPE2",
  "iss": "https://YOUR_APP",
  "exp": 1504807731,
  "iat": 1504804131
}
```
Where:
- sub is the customer identifier. This identifier must be understood by the 4th Platform, so you can get it from an OB identity provider
  or from the 4th Platform User Profile API.
- aud must be https://auth.default-aws-nightly.e2etest.baikalplatform.com/, since this JWT is emitted
  for the 4th Platform.
- scope is the scopes your app want to request authorization for, separated by a space.
- iss is the issuer identifier specified when you got credentials for you app.
- exp is the timestamp (UNIX epoch time format) when the JWT will expire.
- iat is the timestamp (UNIX epoch time format) when the JWT is issued.

To create the signature part you have to sign the encoded header and the encoded payload (joined with a dot)
using the algorithm specified in the header with the private key corresponding to the public key provisioned
when getting credentials for the app.

```
RSASHA256(base64UrlEncode(header) + "." + base64UrlEncode(payload), private_key)
```

Finally concatenate the Base64 representation of the three parts separated by dots.

```
eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.
eyJzdWIiOiI0MTJkNjA2Zi00OTM3LTQ0M2ItYjVlNy1hOGQwZjYzZWYwYmMiLCJhdWQiOiJodHRwczovL2F1dGguZXhhbXBsZS5jb20vIiwic2NvcGUiOiJ1c2VycHJvZmlsZTpyZWFkIiwiaXNzIjoiaHR0cHM6Ly9ZT1VSX0FQUCIsImV4cCI6MTUwNDgwNzczMSwiaWF0IjoxNTA0ODA0MTMxfQ.
Vbpa57mNSHEGO-Eip3whU1TSY5MSkewhOR99CiX4rW9LylJq0-_B7FQdMmFrm1_xIEAb9bwJfDqljQh8j2C1svliRqkGNquu7sfN8Mmw4qK1b9mzDHTgza6OZD7Qnisjkf8sTTC5KiGJ7W4S9sTIsIhOA5O047tZ2fpC3m7GHE-trOfVTmSqT9KlIBkOVdQuEhbvv7VMn2P_E1YxtMADFKaDxFLuYBh2k8qZHvhldg4NOXzQD3TXtMGAa1QwZQTm4VEAOCtS1IJ0jH-drYs33glnfDrSIupZovlpqJMS9X37haycz3IPahRrRxLwsqrIqJdl56hiLNGaza40SVBLWg
```
