import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import Home from '../src/app/page';
import { http } from 'msw';
import { setupServer } from 'msw/node';

const server = setupServer(
  http.post('http://localhost:4000/api/sequential', () => {
    return new Response(JSON.stringify({ data: { word: 10 }, elapsed_time: 100, file_processing_time: 50, algorithm_processing_time: 50 }), {
        headers: {
          'Content-Type': 'application/json',
        },
      })
  }),
  http.post('http://localhost:4000/api/comparison', () => {
    return new Response(JSON.stringify({ sequential: { T1: 10, T2: 20, T3: 30 }, javaStream: { T1: 15, T2: 25, T3: 35 }, forkJoin: { T1: 20, T2: 30, T3: 40 } }), {
        headers: {
          'Content-Type': 'application/json',
        },
      })
  })
);

beforeAll(() => server.listen());
afterEach(() => server.resetHandlers());
afterAll(() => server.close());

describe('Home', () => {
  it('renders upload form and handles file upload', async () => {
    render(<Home />);

    const input = screen.getByLabelText(/text file/i);
    const file = new File(['hello'], 'hello.txt', { type: 'text/plain' });

    fireEvent.change(input, { target: { files: [file] } });

    const button = screen.getByText(/upload/i);
    fireEvent.click(button);

    await waitFor(() => expect(screen.getByText(/✅ file uploaded successfully/i)).toBeInTheDocument());
  });

  it('renders and handles compare button', async () => {
    render(<Home />);

    const input = screen.getByLabelText(/text file/i);
    const file = new File(['hello'], 'hello.txt', { type: 'text/plain' });

    fireEvent.change(input, { target: { files: [file] } });

    const compareButton = screen.getByText(/compare/i);
    fireEvent.click(compareButton);

    await waitFor(() => expect(screen.getByText(/✅ file uploaded successfully/i)).toBeInTheDocument());
    expect(screen.getByText(/elapsed time of each algorithm/i)).toBeInTheDocument();
  });
});
