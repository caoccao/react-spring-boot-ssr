/*
 * Copyright (c) 2024-2025. caoccao.com Sam Cao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
