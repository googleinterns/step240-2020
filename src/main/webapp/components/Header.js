import * as React from "react";
import { Navigation } from "./Navigation";

const OPTIONS = [
  {
    text: 'Docs',
    link: 'https://github.com/googleinterns/step240-2020'
  },
  {
    text: 'Buildbot',
    link: 'http://lab.llvm.org:8011/'
  }
];

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
        <Navigation options={OPTIONS}/>
      </div>
    </header>
  );
}
