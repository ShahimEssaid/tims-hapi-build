# TIMS HAPI source code

This repository is the source code for the TIMS HAPI instance(s). Briefly:

- It depends on a custom Maven build and deploy of the [HAP JPA Starter](https://github.com/hapifhir/hapi-fhir-jpaserver-starter)
  - This custom build [happens here](https://github.com/ShahimEssaid/hapi-fhir-jpaserver-starter-builds) and will be further documented at that repository.
- It depends on a general HAPI extension approach through Spring and Spring Boot capabilities. This approach is being [developed here](https://github.com/ShahimEssaid/hapi-extensions/tree/dev) and used in this project.
- Additional TIMS specific HAPI components are being developed in this repository to implement TIMS FHIR use cases and plugged into the HAPI runtime with the above HAPI extensions library.
- Further documentation will be coming soon.
