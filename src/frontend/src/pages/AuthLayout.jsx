import React from 'react';
import Footer from '../Components/Footer/Footer';
import bluemoonLogo from '../assets/logo/bluemoon_logo.svg';
import './AuthLayout.css';

export default function AuthLayout({ title, subtitle, children }) {
  return (
    <div className="auth-shell">
      <div className="auth-main">
        <div className="auth-card">
          <div className="auth-brand">
            <img className="auth-brand-logo" src={bluemoonLogo} alt="Bluemoon" />
            <div className="auth-brand-text">
              <div className="auth-brand-name">Bluemoon</div>
              <div className="auth-brand-tagline">Quản lý thu phí chung cư</div>
            </div>
          </div>

          <h1 className="auth-title">{title}</h1>
          {subtitle ? <p className="auth-subtitle">{subtitle}</p> : null}

          <div className="auth-content">{children}</div>
        </div>
      </div>

      <Footer />
    </div>
  );
}
