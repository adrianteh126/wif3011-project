import React from 'react';
import { render } from '@testing-library/react';
import Layout from '../src/app/layout';

describe('RootLayout', () => {
  it('renders children correctly', () => {
    const { getByText } = render(
      <Layout>
        <div>Test Child</div>
      </Layout>
    );

    expect(getByText('Test Child')).toBeInTheDocument();
  });
});
