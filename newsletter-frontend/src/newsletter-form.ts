import { LitElement, html, css } from 'lit';
import { customElement, state } from 'lit/decorators.js';

@customElement('newsletter-form')
export class NewsletterForm extends LitElement {
  static styles = css`
    :host {
      display: flex;
      justify-content: center;
      padding: 2rem;
      font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
      background: #f9fafb;
      min-height: 100vh;
    }

    form {
      background: white;
      padding: 2rem 2.5rem;
      border-radius: 12px;
      box-shadow: 0 6px 15px rgba(0, 0, 0, 0.1);
      width: 100%;
      max-width: 400px;
      box-sizing: border-box;
    }

    h2 {
      margin: 0 0 1.5rem 0;
      color: #1f2937;
      font-weight: 700;
      text-align: center;
      font-size: 1.75rem;
    }

    input {
      width: 100%;
      padding: 0.65rem 1rem;
      margin-bottom: 1rem;
      border: 1.8px solid #d1d5db;
      border-radius: 8px;
      font-size: 1rem;
      transition: border-color 0.3s ease, box-shadow 0.3s ease;
      box-sizing: border-box;
    }

    input:focus {
      outline: none;
      border-color: #2563eb; /* blue-600 */
      box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.3);
    }

    button {
      width: 100%;
      padding: 0.75rem;
      background-color: #2563eb;
      color: white;
      font-weight: 600;
      border: none;
      border-radius: 8px;
      cursor: pointer;
      font-size: 1.1rem;
      transition: background-color 0.3s ease;
    }

    button:hover {
      background-color: #1d4ed8; /* blue-700 */
    }

    .error, .success {
      padding: 0.75rem 1rem;
      margin-bottom: 1rem;
      border-radius: 8px;
      font-weight: 600;
      text-align: center;
      box-sizing: border-box;
    }

    .error {
      background-color: #fee2e2; /* red-100 */
      color: #b91c1c; /* red-700 */
      border: 1.5px solid #fca5a5;
    }

    .success {
      background-color: #dcfce7; /* green-100 */
      color: #15803d; /* green-700 */
      border: 1.5px solid #86efac;
    }
  `;

  @state()
  private firstName = '';

  @state()
  private lastName = '';

  @state()
  private email = '';

  @state()
  private errorMsg = '';

  @state()
  private successMsg = '';

  private validateEmail(email: string) {
    const re = /\S+@\S+\.\S+/;
    return re.test(email);
  }

  private async submitHandler(e: Event) {
    e.preventDefault();
    this.errorMsg = '';
    this.successMsg = '';

    if (!this.firstName.trim()) {
      this.errorMsg = 'First name is required';
      return;
    }
    if (!this.lastName.trim()) {
      this.errorMsg = 'Last name is required';
      return;
    }
    if (!this.email.trim() || !this.validateEmail(this.email)) {
      this.errorMsg = 'Valid email is required';
      return;
    }

    try {
      const res = await fetch('http://localhost:8081/api/newsletter/subscribe', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          firstName: this.firstName,
          lastName: this.lastName,
          email: this.email
        })
      });

      const data = await res.json();

      if (!res.ok) {
        this.errorMsg = data.error || 'Subscription failed';
      } else {
        this.successMsg = `Subscribed successfully! Thank you, ${data.firstName}`;
        this.firstName = '';
        this.lastName = '';
        this.email = '';
      }
    } catch (err: any) {
      console.error('Subscription error:', err);
      this.errorMsg = err.message || 'Network error. Please try again later.';
    }
  }

  render() {
    return html`
      <form @submit=${this.submitHandler} novalidate>
        <h2>Subscribe to our Newsletter</h2>
        ${this.errorMsg ? html`<div class="error">${this.errorMsg}</div>` : ''}
        ${this.successMsg ? html`<div class="success">${this.successMsg}</div>` : ''}
        <input
          type="text"
          placeholder="First Name"
          .value=${this.firstName}
          @input=${(e: Event) => (this.firstName = (e.target as HTMLInputElement).value)}
          required
          autocomplete="given-name"
        />
        <input
          type="text"
          placeholder="Last Name"
          .value=${this.lastName}
          @input=${(e: Event) => (this.lastName = (e.target as HTMLInputElement).value)}
          required
          autocomplete="family-name"
        />
        <input
          type="email"
          placeholder="Email"
          .value=${this.email}
          @input=${(e: Event) => (this.email = (e.target as HTMLInputElement).value)}
          required
          autocomplete="email"
        />
        <button type="submit" aria-label="Subscribe to newsletter">Subscribe</button>
      </form>
    `;
  }
}
