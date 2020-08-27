import * as React from "react";
import { Navigation } from "./Navigation";

/**
 * Main Navigation Component
 */
export const Header = () => {
  const TITLE = 'LLVM Build View';

  return (
    <header className='header'>
      <Wrapper>
        <span className='title'>{TITLE}</span>
      </Wrapper>
      <Wrapper>
        <Navigation/>
      </Wrapper>
    </header>
  );
}
