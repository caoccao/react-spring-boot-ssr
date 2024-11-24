import { Button, Card, CardActions, CardContent, Container, Typography } from '@mui/material'

import './App.css'

const App = () => {
  return (
    <div className="App">
      <Container maxWidth="sm">
        <Card sx={{ minWidth: 275, textAlign: 'left' }}>
          <CardContent>
            <Typography gutterBottom sx={{ color: 'text.secondary', fontSize: 14 }}>
              Hello SSR!
            </Typography>
            <Typography variant="h5" component="div">
              React + Spring Boot + Javet
            </Typography>
            <Typography variant="body2">
              This is an SSR demo of React + Spring Boot with Javet.
            </Typography>
          </CardContent>
          <CardActions>
            <Button size="small">Learn More</Button>
          </CardActions>
        </Card>
      </Container>
    </div>
  )
}

export { App }
