import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { register as registerApi } from '../api/authApi';
import AuthLayout from './AuthLayout';

export default function RegisterPage() {
  const navigate = useNavigate();

  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [email, setEmail] = useState('');
  const [identityNumber, setIdentityNumber] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const onSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      await registerApi({
        username,
        password,
        email,
        identity_number: identityNumber,
        role: 'Citizen',
      });
      
      navigate('/login', { replace: true });
    } catch (err) {
      setError(err?.message || 'Đăng ký thất bại');
    } finally {
      setLoading(false);
    }
  };

  return (
    <AuthLayout title="Đăng ký" subtitle="Tạo tài khoản mới.">
      {error ? <div className="auth-error">{error}</div> : null}

      <form onSubmit={onSubmit} style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
        <div className="auth-field">
          <label className="auth-label">Tên đăng nhập</label>
          <input
            className="auth-input"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            autoComplete="username"
            required
          />
        </div>

        <div className="auth-field">
          <label className="auth-label">Mật khẩu</label>
          <input
            type="password"
            className="auth-input"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            autoComplete="new-password"
            required
          />
        </div>

        <div className="auth-field">
          <label className="auth-label">Email</label>
          <input
            type="email"
            className="auth-input"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            autoComplete="email"
            required
          />
        </div>

        <div className="auth-field">
          <label className="auth-label">Số CCCD/CMND</label>
          <input
            className="auth-input"
            value={identityNumber}
            onChange={(e) => setIdentityNumber(e.target.value)}
            required
          />
        </div>

        <button type="submit" disabled={loading} className="auth-submit">
          {loading ? 'Đang tạo tài khoản…' : 'Tạo tài khoản'}
        </button>
      </form>

      <div className="auth-link">
        Đã có tài khoản? <Link to="/login">Đăng nhập</Link>
      </div>
    </AuthLayout>
  );
}
