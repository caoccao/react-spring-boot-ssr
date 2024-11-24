import { render, screen } from '@testing-library/react'

import { App } from '.'

describe('App', () => {
  test('should return the correct text', () => {
    render(<App />)

    expect(screen.getByText('React + Spring Boot + Javet')).toBeInTheDocument()
  })
})
