import express from 'express';

const app = express();

app.use(express.json());

//Set the port that you want the server to run on
const PORT = 4000;

//creates an endpoint for the route /api
app.get('/api', (req, res) => {
  res.json({ message: 'Hello from ExpressJS' });
});

// console.log that your server is up and running
app.listen(PORT, () => {
  console.log(`Server listening on ${PORT}`);
});

let mockScores = [
  { id: 1, name: 'Marlin', score: 100 },
  { id: 2, name: 'Nemo', score: 200 },
  { id: 3, name: 'Dory', score: 300 }
];

app.get('/api/scores', (req, res) => {
  res.json({ scores: mockScores });
});