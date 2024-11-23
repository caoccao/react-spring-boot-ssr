import { StrictMode } from 'react'
import ReactDOMServer from 'react-dom/server'

import { App } from './App'

export function render(componentName: string, propertiesString: string) {
  let Component
  const props = propertiesString ? JSON.parse(propertiesString) : {}
  switch (componentName) {
    case 'App':
      Component = App
      break
    default:
      throw new Error(`Unknown component: ${componentName}`)
  }
  return ReactDOMServer.renderToString(
    <StrictMode>
      <Component {...props} />
    </StrictMode>,
  )
}
