import { useState } from 'react';

const CALC_ENDPOINT = import.meta.env.VITE_CALC_ENDPOINT ?? '/calc';

export default function App() {
  const [expression, setExpression] = useState('');
  const [result, setResult] = useState('');
  const [errorMessage, setErrorMessage] = useState('');
  const [isCalculating, setIsCalculating] = useState(false);

  async function handleSubmit(event) {
    event.preventDefault();
    setErrorMessage('');
    setResult('');
    setIsCalculating(true);

    try {
      const response = await fetch(CALC_ENDPOINT, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ expr: expression })
      });

      if (!response.ok) {
        throw new Error(`Request failed with status ${response.status}`);
      }

      const returnedRes = await response.text();//Need to use .text() when it return a string
      //console.log(response);
      setResult(returnedRes);
    } catch (error) {
      setErrorMessage(error.message);
    } finally {
      setIsCalculating(false);
    }
  }

  return (
      <main style={{ maxWidth: 420, margin: '4rem auto', fontFamily: 'sans-serif' }}>
        <h1>Calculator</h1>

        <form onSubmit={handleSubmit}>
          <input
              type="text"
              value={expression}
              onChange={(event) => setExpression(event.target.value)}
              placeholder="(1+3)*6/3"
              style={{ width: '100%', padding: '0.5rem', fontSize: '1rem' }}
          />
          <button
              type="submit"
              disabled={isCalculating || expression.trim() === ''}
              style={{ marginTop: '0.75rem', padding: '0.5rem 1rem' }}
          >
            {isCalculating ? 'Calculating…' : 'Calculate'}
          </button>
        </form>

        {result !== '' && <p>Result: <strong>{result}</strong></p>}
        {errorMessage !== '' && <p style={{ color: 'crimson' }}>{errorMessage}</p>}
      </main>
  );
}