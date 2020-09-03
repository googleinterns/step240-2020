import * as React from "react";
import { Navigation } from "./Navigation";

/**
 * Main Navigation Component
 */
export const Header = () => {
  const TITLE = 'LLVM Build View';

  return (
    <header className='header'>
      <div>
        <span className='title'>{TITLE}</span>
      </div>
      <div>
        <Navigation/>
      </div>
    </header>
  );
}
