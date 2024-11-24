# react-spring-boot-ssr

This project is an SSR demo of React + Spring Boot with Javet

## Development

* Install Node.js v22+
* Install Bun v1.1+
* Install pnpm
* Install dependencies

```bash
cd src-react
pnpm install
```

### Run Dev

```bash
cd src-react
pnpm dev
```

### Build SSR

```bash
cd src-react
pnpm build
```

## Test

* Start the Spring Boot application
* Visit the following URL in your browser
  * [Render by CJS](http://localhost:8080/render-by-cjs)
  * [Render by ESM](http://localhost:8080/render-by-esm)
