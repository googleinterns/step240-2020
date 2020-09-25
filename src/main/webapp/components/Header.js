// Copyright 2020 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

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
export const Header = _ => {
  const TITLE = 'LLVM Build View';

  return (
    <header className='header'>
      <span className='title'>{TITLE}</span>
      <Navigation options={OPTIONS}/>
    </header>
  );
}
