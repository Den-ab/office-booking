<!DOCTYPE HTML>
<html>
<head>
  <style>
      body {
          padding: 0;
          margin: 0;
          background-color: #f8f8f8;
          color: #2d2d2d;
          font-size: 12px;
          line-height: 24px;
          font-family: Verdana, sans-serif;
      }

      .container {
          width: 610px;
          margin: auto;
          padding-top: 40px;
          padding-bottom: 40px;
          box-sizing: border-box;
      }

      .logo {
          height: 32px;
      }

      .main {
          width: 610px;
          margin: auto;
          border: 1px solid #f0f0f0;
          border-bottom: 1px solid #FFFFFF;
          background-color: #FFFFFF;
      }

      .content {
          margin: 40px;
          margin-bottom: 32px;
      }

      .footer {
          width: 610px;
          margin: auto;
          border: 1px solid #f0f0f0;
          background-color: #FFFFFF;
      }

      .footer-content {
          margin: 40px;
          margin-top: 32px;
      }

      .signature {
          width: 50%;
          float: left;
          text-align: left;
      }

      .contact {
          width: 50%;
          float: right;
          text-align: right;
      }

      .link {
          color: #00af7d;
          text-decoration: none;
      }

      .clear {
          clear: both;
      }
  </style>
</head>
<body>
<div class="container">
  <img class="logo" src="cid:logo_cid" alt="logo">
</div>

<div class="main">
  <div class="content">
    <p>
      ${i18nService.getMessage("email.templates.bookingTemplate.message.line1", tableNumber, bookingDate, spaceName)}
      ${i18nService.getMessage("email.templates.bookingTemplate.message.line2")}
    </p>
  </div>
</div>

<div class="footer">
  <div class="footer-content">
    <p>
        ${i18nService.getMessage("email.templates.baseTemplate.furtherQuestionsMessage.line1")}
      <a class="link" href="mailto:${i18nService.getMessage("email.templates.helpDeskAddress")}">
          ${i18nService.getMessage("email.templates.baseTemplate.furtherQuestions.link")}
      </a>
        ${i18nService.getMessage("email.templates.baseTemplate.furtherQuestionsMessage.line2")}
    </p>
  </div>
</div>

<div class="container">
  <div class="signature">
    <br>
      ${i18nService.getMessage("email.templates.baseTemplate.signature.line1")}
    <br>
      ${i18nService.getMessage("email.templates.baseTemplate.signature.line2")}
    <br>
      ${i18nService.getMessage("email.templates.baseTemplate.signature.line3")}
  </div>

  <div class="contact">
    <br>
    <a class="link" href="https://${i18nService.getMessage("email.templates.siteAddress")}">
        ${i18nService.getMessage("email.templates.siteAddress")}
    </a>
    <br>
    <a class="link" href="mailto:${i18nService.getMessage("email.templates.helpDeskAddress")}">
        ${i18nService.getMessage("email.templates.helpDeskAddress")}
    </a>
    <br>
    <a class="link" href="tel:${i18nService.getMessage("email.templates.contactPhoneNumber")}">
        ${i18nService.getMessage("email.templates.contactPhoneNumber")}
    </a>
  </div>

  <div class="clear"></div>
</div>
</body>
</html>
