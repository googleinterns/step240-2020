import * as React from "react";
import { Navigation } from "./Navigation";

/**
 * Main Navigation Component
 */
export const Header = () => {
  const TITLE = "LLVM Build View";

  return (
    <header>
      <Wrapper>
        <span className="title">{TITLE}</span>
      </Wrapper>
      <Navigation/>
    </header>
  );
}
